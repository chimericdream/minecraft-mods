package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creaking.class)
public class EG$CreakingMixin {
    @Redirect(
        method = "checkCanMove",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/creaking/Creaking;canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z")
    )
    private boolean eg$ignorePlayersWearingFullResinTrim(Creaking instance, LivingEntity target) {
        return instance.canAttack(target) && !(target instanceof Player player && TrimSetUtils.isWearingFullTrim(player, TrimMaterials.RESIN));
    }
}
