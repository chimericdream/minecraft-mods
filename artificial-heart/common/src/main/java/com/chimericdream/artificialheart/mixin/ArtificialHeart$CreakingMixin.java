package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.PassiveCreakingAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Golem-built creakings (see PaleCarvedPumpkinBlock) mark themselves passive via
 * {@link PassiveCreakingAccessor}. A naturally-spawned creaking bound to a real Creaking Heart is never
 * marked, so it is completely unaffected by this mixin.
 *
 * <p>Passive creakings keep the full vanilla stalking behavior (activating, chasing, freezing in place
 * when watched) - only the actual attack, which deals damage, is suppressed. This lets one walk right up
 * to a player and stand there without ever hurting them.
 */
@Mixin(Creaking.class)
public class ArtificialHeart$CreakingMixin implements PassiveCreakingAccessor {
    @Unique
    private static final String AH_PASSIVE_KEY = "artificial_heart_passive";

    @Unique
    private boolean ah$passive = false;

    @Override
    public void ah$setPassive(boolean passive) {
        this.ah$passive = passive;
    }

    @Override
    public boolean ah$isPassive() {
        return this.ah$passive;
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void ah$preventAttackWhenPassive(final ServerLevel level, final Entity target, final CallbackInfoReturnable<Boolean> cir) {
        if (this.ah$passive) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void ah$writePassive(final ValueOutput output, final CallbackInfo ci) {
        output.putBoolean(AH_PASSIVE_KEY, this.ah$passive);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ah$readPassive(final ValueInput input, final CallbackInfo ci) {
        this.ah$passive = input.getBooleanOr(AH_PASSIVE_KEY, false);
    }
}
