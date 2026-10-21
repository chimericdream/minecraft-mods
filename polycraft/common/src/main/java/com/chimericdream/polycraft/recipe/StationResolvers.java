package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.StationViewers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;

import java.util.Optional;
import java.util.function.Function;

/** Recipe choice for the Crafter and the campfire, which have no input slot a cook can be remembered against. */
public final class StationResolvers {
    private StationResolvers() {
    }

    /**
     * What a Crafter should craft. Used for the preview in its screen (with the viewer's choices) and when it crafts,
     * where the person using it, if anyone has it open, is the acting player. With nobody, it is the pin, then the pack
     * default, then vanilla.
     */
    public static Optional<ConflictService.Selection<CraftingRecipe>> crafter(
        ServerLevel level,
        CrafterBlockEntity crafter,
        CraftingInput input,
        Optional<RecipeHolder<CraftingRecipe>> vanilla,
        Function<String, Optional<String>> choices
    ) {
        return ConflictService.select(level, RecipeType.CRAFTING, input, vanilla.orElse(null), choices, ((PinHolder) crafter).polycraft$pins());
    }

    /** The Crafter's choices when it crafts: whoever has it open, else none. */
    public static Function<String, Optional<String>> crafterChoices(ServerLevel level, CrafterBlockEntity crafter) {
        ServerPlayer acting = StationViewers.acting(level, crafter);
        return acting == null ? key -> Optional.empty() : PolyCraftNetworking.choicesFor(acting);
    }

    /**
     * What a campfire cooks a food into: the pack's default for the group if it has one, otherwise vanilla's pick.
     * Campfires have no screen and no viewer, so nobody's own choice applies.
     */
    public static Optional<RecipeHolder<CampfireCookingRecipe>> campfire(
        ServerLevel level,
        SingleRecipeInput input,
        Optional<RecipeHolder<CampfireCookingRecipe>> vanilla
    ) {
        return ConflictService.select(level, RecipeType.CAMPFIRE_COOKING, input, vanilla.orElse(null), key -> Optional.empty(), null)
            .map(ConflictService.Selection::chosen)
            .or(() -> vanilla);
    }
}
