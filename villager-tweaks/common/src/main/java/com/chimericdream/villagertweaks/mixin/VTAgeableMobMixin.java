package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AgeableMob.class)
public abstract class VTAgeableMobMixin {
    @Inject(method = "getBabyStartAge", at = @At("RETURN"), cancellable = true)
    private void vt$modifyGrowUpTime(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof Villager) {
            VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

            if (config.enableGrowUpTimeOverride) {
                cir.setReturnValue(-config.growUpTime);
            }
        }
    }
}

