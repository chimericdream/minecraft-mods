package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class EG$LivingEntityMixin {
    // Echo Shard + Ward sonic boom reduction stacks additively, capped so it never exceeds Echo Shard's old solo 75%.
    private static final float ECHO_SHARD_SONIC_BOOM_REDUCTION = 0.25F;
    private static final float WARD_SONIC_BOOM_REDUCTION = 0.5F;
    private static final float MAX_SONIC_BOOM_REDUCTION = 0.75F;

    @ModifyVariable(
        method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At("HEAD"),
        argsOnly = true,
        name = "damage"
    )
    private float eg$checkForDamageReduction(float damage, ServerLevel level, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        Entity directAttacker = source.getDirectEntity();

        float multiplier = 1.0F;

        if ((directAttacker instanceof EvokerFangs || directAttacker instanceof Vex) && TrimSetUtils.isWearingFullPattern(self, TrimPatterns.VEX)) {
            multiplier *= 0.5F;
        }

        if (source.is(DamageTypes.SONIC_BOOM)) {
            float reduction = 0.0F;
            if (TrimSetUtils.isWearingFullTrim(self, Trims.ECHO_SHARD_TRIM_ID)) {
                reduction += ECHO_SHARD_SONIC_BOOM_REDUCTION;
            }
            if (TrimSetUtils.isWearingFullPattern(self, TrimPatterns.WARD)) {
                reduction += WARD_SONIC_BOOM_REDUCTION;
            }
            multiplier *= 1.0F - Math.min(reduction, MAX_SONIC_BOOM_REDUCTION);
        }

        if (source.is(DamageTypes.INDIRECT_MAGIC) && directAttacker instanceof Guardian
            && TrimSetUtils.isWearingFullTrim(self, Trims.PRISMARINE_SHARD_TRIM_ID)) {
            multiplier *= 0.5F;
        }

        damage *= multiplier;

        if (source.getEntity() instanceof Player attacker) {
            if (self instanceof EnderMan && TrimSetUtils.isWearingFullPattern(attacker, TrimPatterns.EYE)) {
                damage *= 1.5F;
            } else if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(self.getType()).is(EntityTypeTags.UNDEAD) && TrimSetUtils.isWearingFullPattern(attacker, TrimPatterns.RAISER)) {
                damage *= 1.5F;
            } else if (self instanceof Pillager && TrimSetUtils.isWearingFullPattern(attacker, TrimPatterns.SENTRY)) {
                damage *= 1.5F;
            }
        }

        return damage;
    }

    @Inject(
        method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At("RETURN")
    )
    private void eg$applyDuneWeakness(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        if (source.getDirectEntity() == source.getEntity()
            && source.getEntity() instanceof Player attacker
            && TrimSetUtils.isWearingFullPattern(attacker, TrimPatterns.DUNE)) {
            LivingEntity self = (LivingEntity) (Object) this;
            self.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, true, true));
        }
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void eg$blockEffectsFromPatternImmunity(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (effect.is(MobEffects.WITHER) && TrimSetUtils.isWearingFullPattern(self, TrimPatterns.RIB)) {
            cir.setReturnValue(false);
        } else if (effect.is(MobEffects.LEVITATION) && TrimSetUtils.isWearingFullPattern(self, TrimPatterns.SPIRE)) {
            cir.setReturnValue(false);
        } else if (effect.is(MobEffects.MINING_FATIGUE) && TrimSetUtils.isWearingFullPattern(self, TrimPatterns.TIDE)) {
            cir.setReturnValue(false);
        } else if (effect.is(MobEffects.POISON) && TrimSetUtils.isWearingFullPattern(self, TrimPatterns.WILD)) {
            cir.setReturnValue(false);
        }
    }

    // Swapping gear can grant pattern immunity to an effect the entity already has (e.g. mining fatigue
    // from Elder Guardians, then equipping a full Tide set) - canBeAffected only blocks new applications,
    // so also strip any currently active effect it no longer qualifies for.
    @Inject(method = "tickEffects", at = @At("TAIL"))
    private void eg$removeEffectsBlockedByPatternImmunity(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self.level() instanceof ServerLevel)) {
            return;
        }

        List<Holder<MobEffect>> toRemove = new ArrayList<>();
        for (MobEffectInstance effect : self.getActiveEffects()) {
            if (!self.canBeAffected(effect)) {
                toRemove.add(effect.getEffect());
            }
        }

        for (Holder<MobEffect> effect : toRemove) {
            self.removeEffect(effect);
        }
    }
}
