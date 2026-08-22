package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EG$EndermanMixin {
    @Inject(method = "isBeingStaredBy", at = @At("HEAD"), cancellable = true)
    private void eg$ignoreGazeFromFullEnderPearlTrim(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (TrimSetUtils.isWearingFullTrim(player, Trims.ENDER_PEARL_TRIM_ID)) {
            cir.setReturnValue(false);
        }
    }
}
