package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public class EG$IronGolemMixin {
    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void eg$ignorePlayersWearingFullIronTrim(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && TrimSetUtils.isWearingFullTrim(player, TrimMaterials.IRON)) {
            cir.setReturnValue(false);
        }
    }
}
