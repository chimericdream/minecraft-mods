package com.chimericdream.bettertargetdummies.mixin;

import com.chimericdream.bettertargetdummies.entity.TargetDummyMarker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class BTD$LivingEntityMixin {
    @Shadow
    protected abstract float getDamageAfterArmorAbsorb(DamageSource damageSource, float damage);

    @Shadow
    protected abstract float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage);

    // hurtServer plays the hurt sound and sets hurtTime (the red hit-flash/knockback tilt) itself,
    // unconditionally, right after calling actuallyHurt -- cancelling actuallyHurt alone (below) stops
    // the health change but does nothing about those, so an environmental source (rain, dry-out,
    // drowning, ...) still visibly and audibly "hurts" the dummy even though it never takes damage.
    // Player-dealt hits are deliberately let through this check: that sound/flash/knockback is the
    // whole point of testing an attack against the dummy, only the actual health change is unwanted.
    @Inject(
        method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void btd$suppressEnvironmentalHurtFeedback(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!TargetDummyMarker.isDummy(self)) {
            return;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || TargetDummyMarker.isFromPlayer(source)) {
            return;
        }

        cir.setReturnValue(false);
    }

    // actuallyHurt is where armor/magic absorption is applied and health is actually reduced, so it's
    // the only place the "real" post-mitigation damage number (what a player wants to see when testing
    // an attack) is available. Cancelling here — rather than after health drops — keeps hurtServer's
    // sound/knockback/hit-flash feedback intact while the dummy's health never actually changes. Only
    // reachable for player hits and /kill-or-void now; every other source is already stopped above.
    @Inject(
        method = "actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void btd$reportAndCancelDummyDamage(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!TargetDummyMarker.isDummy(self)) {
            return;
        }

        // /kill and void damage stay lethal even for a dummy — a manual escape hatch alongside the
        // redstone on/off toggle, since a stuck dummy shouldn't require breaking the block to remove.
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        float finalDamage = this.getDamageAfterArmorAbsorb(source, dmg);
        finalDamage = this.getDamageAfterMagicAbsorb(source, finalDamage);
        TargetDummyMarker.reportDamage(self, source, finalDamage);
        ci.cancel();
    }
}
