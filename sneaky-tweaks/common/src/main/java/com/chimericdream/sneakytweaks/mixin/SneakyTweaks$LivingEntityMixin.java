package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.campfire.CampfireSneakingLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SneakyTweaks$LivingEntityMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void st$tickCampfireGrace(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player player && self.level() instanceof ServerLevel) {
            CampfireSneakingLogic.tick(player);
        }
    }
}
