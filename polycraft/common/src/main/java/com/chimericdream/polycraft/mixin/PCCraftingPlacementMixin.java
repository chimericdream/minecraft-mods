package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.recipe.RecipeIndex;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Clicking a recipe in the recipe book on a crafting grid counts as choosing that recipe. */
@Mixin(AbstractCraftingMenu.class)
public class PCCraftingPlacementMixin {
    @Inject(method = "handlePlacement", at = @At("RETURN"))
    private void polycraft$recordChoice(
        boolean useMaxItems,
        boolean allowDroppingItemsToClear,
        RecipeHolder<?> recipe,
        ServerLevel level,
        Inventory inventory,
        CallbackInfoReturnable<RecipeBookMenu.PostPlaceAction> cir
    ) {
        if (inventory.player instanceof ServerPlayer player) {
            PolyCraftNetworking.recordBookChoice(player, (AbstractContainerMenu) (Object) this, RecipeIndex.recipeId(recipe));
        }
    }
}
