package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.config.SyncedConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Removes the "Too Expensive!" limit. {@code createResult} empties the result when the cost is 40 or more
 * and the player is not in creative mode. This mixin treats that check as if the player were in creative.
 */
@Mixin(AnvilMenu.class)
public abstract class EFAnvilMenuMixin {
    /**
     * Ordinal 0 is the creative check that lets incompatible enchantments combine. Ordinal 1 is the cost limit.
     * Vanilla sets the price to 40 when the first input is a stack of more than one item, so that the limit
     * stops the anvil from enchanting the whole stack. Stacks keep the limit.
     */
    @ModifyExpressionValue(
        method = "createResult()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z", ordinal = 1)
    )
    private boolean enchantingfixes$removeCostLimit(boolean original) {
        if (original || !SyncedConfig.effective().removeAnvilLimit) {
            return original;
        }

        return ((AnvilMenu) (Object) this).getSlot(0).getItem().getCount() <= 1;
    }
}
