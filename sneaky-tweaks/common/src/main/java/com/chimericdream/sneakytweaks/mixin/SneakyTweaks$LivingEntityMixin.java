package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.campfire.CampfireSneakingLogic;
import com.chimericdream.sneakytweaks.crouchbridge.CrouchBridgeLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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

    /**
     * Runs on both sides: the server (authoritative for every player) and the client (which locally
     * simulates its own player for movement prediction) both call {@code travel} whenever they're the
     * one driving that player's physics, so holding this hook to that same condition keeps the two in
     * sync without needing to network anything.
     */
    @Inject(method = "travel", at = @At("TAIL"))
    private void st$tickCrouchBridge(Vec3 input, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player player) {
            CrouchBridgeLogic.tick(player);
        }
    }
}
