package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class EG$LivingEntityMixin {
    @ModifyVariable(
        method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At("HEAD"),
        argsOnly = true,
        name = "damage"
    )
    private float eg$reduceSonicBoomDamageWithFullEchoShardTrim(float damage, ServerLevel level, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (source.is(DamageTypes.SONIC_BOOM) && TrimSetUtils.isWearingFullTrim(self, Trims.ECHO_SHARD_TRIM_ID)) {
            return damage * 0.5F;
        }

        return damage;
    }
}
