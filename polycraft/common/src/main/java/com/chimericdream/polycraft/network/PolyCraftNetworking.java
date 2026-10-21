package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.client.ClientConflictState;
import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import com.chimericdream.polycraft.recipe.ConflictService.Option;
import com.chimericdream.polycraft.recipe.PinHolder;
import com.chimericdream.polycraft.recipe.PolyCraftEpoch;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * The server's side of the picker: it remembers what each open menu was last offered (so it can tell the client only
 * when something changed, and so it can check a selection against what was really offered) and applies selections and
 * pins. Everything here runs on the server thread. Entries are keyed by menu, so they disappear when a menu is closed.
 */
public final class PolyCraftNetworking {
    private static final Map<AbstractContainerMenu, Offer> OFFERED = new WeakHashMap<>();
    private static final Map<AbstractContainerMenu, Map<String, String>> SESSION_CHOICES = new WeakHashMap<>();

    private PolyCraftNetworking() {
    }

    /** Registers the payloads. One call from common init registers each type on both sides. */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            SelectRecipePayload.ID,
            SelectRecipePayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> applySelection(player, payload));
                }
            }
        );

        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            SetPinPayload.ID,
            SetPinPayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> applyPin(player, payload));
                }
            }
        );

        NetworkManager.registerReceiver(
            NetworkManager.s2c(),
            ConflictOptionsPayload.ID,
            ConflictOptionsPayload.CODEC,
            (payload, context) -> context.queue(() -> ClientConflictState.set(payload))
        );
    }

    /**
     * The group-to-recipe choices that apply to {@code player} at the menu they have open: what they picked since
     * opening it, otherwise what they have saved.
     */
    public static Function<String, Optional<String>> choicesFor(ServerPlayer player) {
        return choicesFor(player, player.containerMenu);
    }

    /** Like {@link #choicesFor(ServerPlayer)}, for a menu that is being built and isn't the player's open menu yet. */
    public static Function<String, Optional<String>> choicesFor(ServerPlayer player, AbstractContainerMenu menu) {
        PolyCraftChoices saved = PolyCraftChoices.get(player.level().getServer());
        return groupKey -> {
            String session = SESSION_CHOICES.getOrDefault(menu, Map.of()).get(groupKey);
            return session != null ? Optional.of(session) : saved.choiceFor(player.getUUID(), groupKey);
        };
    }

    /** Tells the player's client what it can choose at {@code menu}, if that changed since the last time. */
    public static void sync(ServerPlayer player, AbstractContainerMenu menu, @Nullable Offer offer) {
        Offer previous = OFFERED.get(menu);
        if (sameOffer(previous, offer)) {
            return;
        }

        if (offer == null) {
            OFFERED.remove(menu);
        } else {
            OFFERED.put(menu, offer);
        }

        if (!NetworkManager.canPlayerReceive(player, ConflictOptionsPayload.ID)) {
            return;
        }

        if (offer == null) {
            NetworkManager.sendToPlayer(player, ConflictOptionsPayload.none(menu.containerId));
            return;
        }

        List<ConflictOptionsPayload.Entry> entries = offer.options().stream()
            .map(option -> new ConflictOptionsPayload.Entry(option.id(), option.result(), option.enabled()))
            .toList();
        NetworkManager.sendToPlayer(
            player,
            new ConflictOptionsPayload(menu.containerId, offer.groupKey(), offer.selectedIndex(), entries, offer.pinnable(), offer.pinned())
        );
    }

    /** What the server last offered for {@code menu}, if it offered anything. */
    public static Optional<Offer> offered(AbstractContainerMenu menu) {
        return Optional.ofNullable(OFFERED.get(menu));
    }

    /** Applies a selection the client asked for, if it matches what was offered. Public so tests can drive it. */
    public static void applySelection(ServerPlayer player, SelectRecipePayload payload) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != payload.containerId()) {
            return;
        }

        Offer offer = OFFERED.get(menu);
        if (offer == null || !offer.groupKey().equals(payload.groupKey())) {
            return;
        }

        Optional<Option> chosen = offer.byId(payload.recipeId());
        if (chosen.isEmpty() || !chosen.get().enabled()) {
            return;
        }

        SESSION_CHOICES.computeIfAbsent(menu, key -> new HashMap<>()).put(payload.groupKey(), payload.recipeId());
        PolyCraftChoices.get(player.level().getServer()).choose(player.getUUID(), payload.groupKey(), payload.recipeId());
        PolyCraftEpoch.bump();

        refreshAfterChange(menu);
    }

    /** Makes the station use the new choice or pin straight away. */
    private static void refreshAfterChange(AbstractContainerMenu menu) {
        if (menu instanceof AbstractCraftingMenu || menu instanceof ItemCombinerMenu) {
            // Run the station's own update so it picks the recipe again with the new choice in place. The crafting
            // table and the smithing table both ignore which container is passed, or want their input container,
            // and the first slot of each is in that container.
            menu.slotsChanged(menu.getSlot(0).container);
        } else if (menu instanceof CrafterMenu crafter) {
            crafter.slotChanged(menu, 0, net.minecraft.world.item.ItemStack.EMPTY);
        } else {
            StationViewers.pinHolderOf(menu).ifPresent(PinHolder::polycraft$pinsChanged);
        }
    }

    /** Applies a pin or unpin the client asked for. Public so tests can drive it. */
    public static void applyPin(ServerPlayer player, SetPinPayload payload) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != payload.containerId()) {
            return;
        }

        Offer offer = OFFERED.get(menu);
        Optional<PinHolder> holder = StationViewers.pinHolderOf(menu);
        if (offer == null || !offer.pinnable() || holder.isEmpty() || !offer.groupKey().equals(payload.groupKey())) {
            return;
        }

        if (payload.pinned()) {
            Option selected = offer.selected();
            if (!selected.enabled()) {
                return;
            }

            holder.get().polycraft$pins().pin(payload.groupKey(), selected.id());
        } else {
            holder.get().polycraft$pins().unpin(payload.groupKey());
        }

        PolyCraftEpoch.bump();
        holder.get().polycraft$pinsChanged();
        if (menu instanceof CrafterMenu) {
            refreshAfterChange(menu);
        }
    }

    private static boolean sameOffer(@Nullable Offer a, @Nullable Offer b) {
        if (a == null || b == null) {
            return a == b;
        }

        if (!a.groupKey().equals(b.groupKey()) || a.selectedIndex() != b.selectedIndex() || a.options().size() != b.options().size()
            || a.pinnable() != b.pinnable() || a.pinned() != b.pinned()) {
            return false;
        }

        for (int i = 0; i < a.options().size(); i++) {
            Option left = a.options().get(i);
            Option right = b.options().get(i);
            if (!left.id().equals(right.id()) || left.enabled() != right.enabled() || !ItemStack.matches(left.result(), right.result())) {
                return false;
            }
        }

        return true;
    }
}
