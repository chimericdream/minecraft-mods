package com.chimericdream.effectivegear.ability;

import com.chimericdream.effectivegear.util.PlayerAbilityState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.phys.Vec3;

public class BoltDashAbility implements TrimAbility {
    private static final int COOLDOWN_TICKS = 60;
    private static final double HORIZONTAL_SPEED = 1.4;
    private static final double VERTICAL_SPEED = 0.2;

    @Override
    public ResourceKey<TrimPattern> pattern() {
        return TrimPatterns.BOLT;
    }

    @Override
    public boolean tryActivate(ServerPlayer player) {
        if (!player.onGround() || !player.isSprinting() || !PlayerAbilityState.isBoltDashReady(player)) {
            return false;
        }

        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0, look.z).normalize();
        player.setDeltaMovement(player.getDeltaMovement().add(forward.scale(HORIZONTAL_SPEED)).add(0.0, VERTICAL_SPEED, 0.0));
        player.hurtMarked = true;

        PlayerAbilityState.startBoltDashCooldown(player, COOLDOWN_TICKS);
        player.level().playSound(null, player, SoundEvents.CAMEL_DASH, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
