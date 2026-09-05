package com.chimericdream.camelnostrils.mixin;

import net.minecraft.world.entity.animal.fish.WaterAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterAnimal.class)
public class CN$WaterAnimalMixin {
    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void cn$allowLeashingFish(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
