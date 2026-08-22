package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class EG$PiglinAiMixin {
    @Inject(method = "isWearingSafeArmor", at = @At("HEAD"), cancellable = true)
    private static void eg$treatFullGoldTrimAsSafeArmor(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (TrimSetUtils.isWearingFullTrim(livingEntity, TrimMaterials.GOLD)) {
            cir.setReturnValue(true);
        }
    }
}
