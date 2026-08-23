package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
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
    private float eg$checkForDamageReduction(float damage, ServerLevel level, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Negate lightning damage with full copper trim
        if (source.is(DamageTypes.LIGHTNING_BOLT) && TrimSetUtils.isWearingFullTrim(self, TrimMaterials.COPPER)) {
            return 0.0F;
        }

        // Reduce sonic boom damage with full echo shard trim
        if (source.is(DamageTypes.SONIC_BOOM) && TrimSetUtils.isWearingFullTrim(self, Trims.ECHO_SHARD_TRIM_ID)) {
            return damage * 0.25F;
        }

        return damage;
    }
}
