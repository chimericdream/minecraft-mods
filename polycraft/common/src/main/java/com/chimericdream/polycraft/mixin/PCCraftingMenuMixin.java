package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * The crafting table's and the player's 2x2 grid's recipe lookup: after vanilla picks a recipe, PolyCraft may swap in
 * the one the player chose and tells their client what the choices are.
 */
@Mixin(CraftingMenu.class)
public class PCCraftingMenuMixin {
    @WrapOperation(
        method = "slotChangedCraftingGrid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"
        )
    )
    private static Optional<RecipeHolder<CraftingRecipe>> polycraft$selectRecipe(
        RecipeManager manager,
        RecipeType<CraftingRecipe> type,
        RecipeInput input,
        Level level,
        RecipeHolder<CraftingRecipe> hint,
        Operation<Optional<RecipeHolder<CraftingRecipe>>> original,
        @Local(argsOnly = true) AbstractContainerMenu menu,
        @Local(argsOnly = true) Player player
    ) {
        Optional<RecipeHolder<CraftingRecipe>> vanilla = original.call(manager, type, input, level, hint);
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return vanilla;
        }

        Optional<ConflictService.Selection<CraftingRecipe>> selection = ConflictService.select(
            serverLevel,
            type,
            (CraftingInput) input,
            vanilla.orElse(null),
            PolyCraftNetworking.choicesFor(serverPlayer),
            null
        );

        PolyCraftNetworking.sync(serverPlayer, menu, selection.map(ConflictService.Selection::offer).orElse(null));
        return selection.map(ConflictService.Selection::chosen).or(() -> vanilla);
    }
}
