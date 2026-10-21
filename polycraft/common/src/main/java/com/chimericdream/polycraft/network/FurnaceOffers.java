package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.chimericdream.polycraft.recipe.FurnaceResolver;
import com.chimericdream.polycraft.recipe.PolyCraftEpoch;
import com.chimericdream.polycraft.recipe.RecipeIndex;
import com.chimericdream.polycraft.recipe.StationBlock;
import com.chimericdream.polycraft.rules.PolyCraftRules;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Keeps the picker on a furnace screen up to date. A furnace doesn't tell its menu when things change, so each tick the
 * server checks whether anything that matters has changed for a player who has one open, and only then works out the
 * options again.
 */
public final class FurnaceOffers {
    private static final Map<AbstractContainerMenu, String> STAMPS = new WeakHashMap<>();

    private FurnaceOffers() {
    }

    public static void init() {
        TickEvent.PLAYER_POST.register(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                refresh(serverPlayer);
            }
        });
    }

    /** Works out and sends what {@code player} can choose at the furnace they have open, if it changed. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void refresh(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        Optional<AbstractFurnaceBlockEntity> viewed = StationViewers.furnaceOf(menu);
        if (viewed.isEmpty() || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        AbstractFurnaceBlockEntity furnace = viewed.get();
        ItemStack inputStack = furnace.getItem(0);
        ItemStack outputStack = furnace.getItem(2);
        ServerPlayer acting = StationViewers.acting(level, furnace);

        String stamp = RecipeIndex.resultKey(inputStack) + inputStack.isEmpty()
            + "|" + RecipeIndex.resultKey(outputStack) + outputStack.getCount() + outputStack.isEmpty()
            + "|" + PolyCraftEpoch.get()
            + "|" + (acting == null ? "-" : acting.getUUID())
            + "|" + System.identityHashCode(PolyCraftRules.current())
            + "|" + System.identityHashCode(level.recipeAccess())
            + "|" + PolyCraftConfig.get().detectOverlappingInputs
            + "|" + PolyCraftConfig.get().recipeTypes().test(RecipeIndex.typeId(((StationBlock) furnace).polycraft$recipeType()));
        if (stamp.equals(STAMPS.put(menu, stamp))) {
            return;
        }

        ConflictService.Offer offer = null;
        if (!inputStack.isEmpty()) {
            SingleRecipeInput input = new SingleRecipeInput(inputStack);
            RecipeType type = ((StationBlock) furnace).polycraft$recipeType();
            Optional<RecipeHolder<?>> vanilla = (Optional) level.recipeAccess().getRecipeFor(type, input, level);
            if (vanilla.isPresent()) {
                offer = FurnaceResolver.compute(level, furnace, input, vanilla.get())
                    .map(ConflictService.Selection::offer)
                    .orElse(null);
            }
        }

        PolyCraftNetworking.sync(player, menu, offer);
    }
}
