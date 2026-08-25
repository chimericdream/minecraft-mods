package com.chimericdream.effectivegear.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// Per-player Bolt/Flow ability state and Wayfinder's last-boosted-vehicle tracking; ticked once per player per tick from EG$PlayerMixin.
public final class PlayerAbilityState {
    private static final class State {
        int boltDashCooldown;
        int flowJumpCooldown;
        boolean flowJumpUsedSinceGrounded;
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

    // Decrements cooldowns and resets the once-per-airborne-stint Flow flag once the player lands.
    public static void tick(Player player) {
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
    }

    public static boolean isFlowJumpReady(Player player) {
        State state = stateFor(player);
        return state.flowJumpCooldown <= 0 && !state.flowJumpUsedSinceGrounded;
    }

    public static void startFlowJumpCooldown(Player player, int ticks) {
        State state = stateFor(player);
        state.flowJumpCooldown = ticks;
        state.flowJumpUsedSinceGrounded = true;
    }

    public static UUID getLastSpeedBoostedVehicle(Player player) {
        return stateFor(player).lastSpeedBoostedVehicle;
    }

    public static void setLastSpeedBoostedVehicle(Player player, Entity vehicle) {
        stateFor(player).lastSpeedBoostedVehicle = vehicle == null ? null : vehicle.getUUID();
    }
}
