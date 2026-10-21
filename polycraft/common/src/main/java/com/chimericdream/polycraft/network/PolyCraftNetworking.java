package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.client.ClientConflictState;
import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import com.chimericdream.polycraft.recipe.ConflictService.Option;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
 * when something changed, and so it can check a selection against what was really offered) and applies selections.
 * Everything here runs on the server thread. Entries are keyed by menu, so they disappear when a menu is closed.
 */
public final class PolyCraftNetworking {
    private static final Map<AbstractContainerMenu, Offer> OFFERED = new WeakHashMap<>();
    private static final Map<AbstractContainerMenu, Map<String, String>> SESSION_CHOICES = new WeakHashMap<>();

    private PolyCraftNetworking() {
    }

    /** Registers both payloads. One call from common init registers each type on both sides. */
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
        AbstractContainerMenu menu = player.containerMenu;
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
            .map(option -> new ConflictOptionsPayload.Entry(option.id(), option.result()))
            .toList();
        NetworkManager.sendToPlayer(player, new ConflictOptionsPayload(menu.containerId, offer.groupKey(), offer.selectedIndex(), entries));
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
        if (offer == null || !offer.groupKey().equals(payload.groupKey()) || offer.byId(payload.recipeId()).isEmpty()) {
            return;
        }

        SESSION_CHOICES.computeIfAbsent(menu, key -> new HashMap<>()).put(payload.groupKey(), payload.recipeId());
        PolyCraftChoices.get(player.level().getServer()).choose(player.getUUID(), payload.groupKey(), payload.recipeId());

        // Run the station's own update so it picks the recipe again with the new choice in place.
        menu.slotsChanged(menu.getSlot(0).container);
    }

    private static boolean sameOffer(@Nullable Offer a, @Nullable Offer b) {
        if (a == null || b == null) {
            return a == b;
        }

        if (!a.groupKey().equals(b.groupKey()) || a.selectedIndex() != b.selectedIndex() || a.options().size() != b.options().size()) {
            return false;
        }

        for (int i = 0; i < a.options().size(); i++) {
            Option left = a.options().get(i);
            Option right = b.options().get(i);
            if (!left.id().equals(right.id()) || !ItemStack.matches(left.result(), right.result())) {
                return false;
            }
        }

        return true;
    }
}
