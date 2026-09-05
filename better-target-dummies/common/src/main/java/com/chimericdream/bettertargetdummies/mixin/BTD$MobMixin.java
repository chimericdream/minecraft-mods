package com.chimericdream.bettertargetdummies.mixin;

import com.chimericdream.bettertargetdummies.entity.TargetDummyMarker;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class BTD$MobMixin {
    // burnUndead() is what Mob.aiStep() calls for every EntityTypeTags.BURN_IN_DAYLIGHT mob (zombies,
    // skeletons, ...) to ignite it in direct sunlight. A target dummy shouldn't spontaneously catch
    // fire just for standing outside — cancelling here also spares its sun-blocking helmet, if any,
    // from taking degradation damage it otherwise would.
    @Inject(method = "burnUndead()V", at = @At("HEAD"), cancellable = true)
    private void btd$suppressSunburn(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (TargetDummyMarker.isDummy(self)) {
            ci.cancel();
        }
    }

    // playAmbientSound() is the trigger Mob.baseTick() calls periodically; every vanilla mob picks its
    // actual sound by overriding getAmbientSound() rather than this method, so cancelling it here is a
    // single hook that silences idle noise (chicken clucks, zombie groans, ...) for any mob type
    // without needing a mixin per species. Hurt/death sounds are untouched -- they're triggered
    // separately from LivingEntity.hurtServer, not through here.
    @Inject(method = "playAmbientSound()V", at = @At("HEAD"), cancellable = true)
    private void btd$suppressAmbientSound(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (TargetDummyMarker.isDummy(self)) {
            ci.cancel();
        }
    }
}
