package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VibrationSystem.Listener.class)
public class EG$VibrationSystemListenerMixin {
    @Inject(
        method= "handleGameEvent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/phys/Vec3;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void eg$suppressBlockDestroyVibrationForSilenceTrim(ServerLevel level, Holder<GameEvent> event, GameEvent.Context context, Vec3 sourcePosition, CallbackInfoReturnable<Boolean> cir) {
        if (event.is(GameEvent.BLOCK_DESTROY.key())) {
            if (
                context.sourceEntity() instanceof Player player
                && player.isShiftKeyDown()
                && TrimSetUtils.isWearingFullPattern(player, TrimPatterns.SILENCE)
            ) {
                cir.setReturnValue(false);
            }

            return;
        }

        // Also negate the sound of items falling on the ground (e.g. the block the player just broke)
        if (event.is(GameEvent.HIT_GROUND.key())) {
            Player nearestPlayer = level.getNearestPlayer(sourcePosition.x, sourcePosition.y, sourcePosition.z, 6.0, false);

            if (
                nearestPlayer != null
                && nearestPlayer.isShiftKeyDown()
                && TrimSetUtils.isWearingFullPattern(nearestPlayer, TrimPatterns.SILENCE)
            ) {
                cir.setReturnValue(false);
            }
        }
    }
}
