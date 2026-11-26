package com.chimericdream.enchantingfixes.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The screen shows "Too Expensive!" when the cost is 40 or more and the player is not in creative mode.
 * The server decides if the anvil gives a result (see {@code EFAnvilMenuMixin}). If the result slot has an
 * item, the screen shows the cost. This also works when the client config is different from the server config.
 */
@Mixin(AnvilScreen.class)
public abstract class EFAnvilScreenMixin {
    @ModifyExpressionValue(
        method = "extractLabels",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasInfiniteMaterials()Z")
    )
    private boolean enchantingfixes$showCostAboveLimit(boolean original) {
        return original || ((AnvilScreen) (Object) this).getMenu().getSlot(2).hasItem();
    }
}
