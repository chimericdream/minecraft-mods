package com.chimericdream.effectivegear.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// Per-player Bolt/Flow ability state and Wayfinder's last-boosted-vehicle tracking; ticked once per player per tick from EG$PlayerMixin.
public final class PlayerAbilityState {
    private static final int FLOW_DOUBLE_JUMP_COOLDOWN_TICKS = 20;

    private static final class State {
        int boltDashCooldown;
        int flowJumpCooldown;
        boolean flowJumpUsedSinceGrounded;
        boolean wasJumping;
        UUID lastSpeedBoostedVehicle;
    }

    private static final Map<UUID, State> STATES = new HashMap<>();

    private PlayerAbilityState() {
    }

    private static State stateFor(Player player) {
        return STATES.computeIfAbsent(player.getUUID(), id -> new State());
    }

    public static void remove(Player player) {
        STATES.remove(player.getUUID());
    }

    public static boolean isBoltDashReady(Player player) {
        return stateFor(player).boltDashCooldown <= 0;
    }

    public static void startBoltDashCooldown(Player player, int ticks) {
        stateFor(player).boltDashCooldown = ticks;
    }

    // Edge-checks jump-while-airborne against the stored previous-tick state before overwriting it; returns whether Flow's double jump should trigger.
    public static boolean tick(Player player, boolean wearingFlowTrim) {
        State state = stateFor(player);

        if (state.boltDashCooldown > 0) {
            state.boltDashCooldown--;
        }

        if (state.flowJumpCooldown > 0) {
            state.flowJumpCooldown--;
        }

        if (player.onGround()) {
            state.flowJumpUsedSinceGrounded = false;
        }

        boolean jumpPressedThisTick = player.isJumping() && !state.wasJumping;
        boolean triggerFlowDoubleJump = wearingFlowTrim
            && jumpPressedThisTick
            && !player.onGround()
            && !state.flowJumpUsedSinceGrounded
            && state.flowJumpCooldown <= 0;

        if (triggerFlowDoubleJump) {
            state.flowJumpUsedSinceGrounded = true;
            state.flowJumpCooldown = FLOW_DOUBLE_JUMP_COOLDOWN_TICKS;
        }

        state.wasJumping = player.isJumping();

        return triggerFlowDoubleJump;
    }

    public static UUID getLastSpeedBoostedVehicle(Player player) {
        return stateFor(player).lastSpeedBoostedVehicle;
    }

    public static void setLastSpeedBoostedVehicle(Player player, Entity vehicle) {
        stateFor(player).lastSpeedBoostedVehicle = vehicle == null ? null : vehicle.getUUID();
    }
}
