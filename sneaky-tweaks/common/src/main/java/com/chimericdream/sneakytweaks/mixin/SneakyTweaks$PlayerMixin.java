package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.crouchbridge.CrouchBridgeLogic;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class SneakyTweaks$PlayerMixin {
    @Inject(method = "maybeBackOffFromEdge", at = @At("HEAD"), cancellable = true)
    private void st$allowCrouchBridgeEdge(Vec3 delta, MoverType moverType, CallbackInfoReturnable<Vec3> cir) {
        Player self = (Player) (Object) this;

        if (CrouchBridgeLogic.shouldAllowSteppingOffEdge(self)) {
            cir.setReturnValue(delta);
        }
    }
}
