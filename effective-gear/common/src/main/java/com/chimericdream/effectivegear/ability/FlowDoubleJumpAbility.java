package com.chimericdream.effectivegear.ability;

import com.chimericdream.effectivegear.util.PlayerAbilityState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.phys.Vec3;

public class FlowDoubleJumpAbility implements TrimAbility {
    private static final int COOLDOWN_TICKS = 20;
    private static final double HORIZONTAL_SPEED = 0.65;
    private static final double VERTICAL_SPEED = 0.6;

    @Override
    public ResourceKey<TrimPattern> pattern() {
        return TrimPatterns.FLOW;
    }

    @Override
    public boolean tryActivate(ServerPlayer player) {
        if (player.onGround() || !PlayerAbilityState.isFlowJumpReady(player)) {
            return false;
        }

        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0, look.z).normalize();
        player.setDeltaMovement(player.getDeltaMovement().add(forward.scale(HORIZONTAL_SPEED)).add(0.0, VERTICAL_SPEED, 0.0));
        player.hurtMarked = true;

        PlayerAbilityState.startFlowJumpCooldown(player, COOLDOWN_TICKS);
        player.level().playSound(null, player, SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
