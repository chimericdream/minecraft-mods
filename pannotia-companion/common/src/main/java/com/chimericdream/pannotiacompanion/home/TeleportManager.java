package com.chimericdream.pannotiacompanion.home;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Runs stand-still teleports: a player who asks to teleport must keep still for the configured delay,
 * and cooldowns start once a teleport actually happens.
 */
public final class TeleportManager {
    /** Moving further than this (in blocks) from where the request was made cancels it. */
    private static final double MOVE_TOLERANCE_SQR = 0.1 * 0.1;

    public static final Cooldowns HOME_COOLDOWNS = new Cooldowns();
    public static final Cooldowns SPAWN_COOLDOWNS = new Cooldowns();
    public static final Cooldowns BACK_COOLDOWNS = new Cooldowns();

    private record Pending(Vec3 start, int ticksLeft, int cooldownSeconds, Cooldowns cooldowns, Consumer<ServerPlayer> action) {
        Pending tick() {
            return new Pending(start, ticksLeft - 1, cooldownSeconds, cooldowns, action);
        }
    }

    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    private TeleportManager() {
    }

    public static void init() {
        TickEvent.SERVER_POST.register(TeleportManager::tick);
        PlayerEvent.PLAYER_QUIT.register(player -> PENDING.remove(player.getUUID()));
    }

    /**
     * Starts a teleport for {@code player}. Returns false (after telling the player why) when they are on
     * cooldown; otherwise runs {@code action} now or after {@code delayTicks} of standing still.
     */
    public static boolean request(ServerPlayer player, Cooldowns cooldowns, int delayTicks, int cooldownSeconds, Consumer<ServerPlayer> action) {
        MinecraftServer server = player.level().getServer();
        int remaining = cooldowns.remainingSeconds(player.getUUID(), server.getTickCount());
        if (remaining > 0) {
            player.sendSystemMessage(Component.translatable("message.pannotiacompanion.cooldown", remaining).withStyle(net.minecraft.ChatFormatting.RED));
            return false;
        }

        if (delayTicks <= 0) {
            complete(player, new Pending(player.position(), 0, cooldownSeconds, cooldowns, action));
            return true;
        }

        PENDING.put(player.getUUID(), new Pending(player.position(), delayTicks, cooldownSeconds, cooldowns, action));
        player.sendSystemMessage(Component.translatable("message.pannotiacompanion.hold_still", delayTicks / 20.0).withStyle(net.minecraft.ChatFormatting.GOLD));
        return true;
    }

    private static void complete(ServerPlayer player, Pending pending) {
        pending.cooldowns.start(player.getUUID(), player.level().getServer().getTickCount(), pending.cooldownSeconds);
        pending.action.accept(player);
    }

    private static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, Pending>> it = PENDING.entrySet().iterator();
        Map<UUID, Pending> updated = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<UUID, Pending> entry = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            it.remove();

            if (player == null) {
                continue;
            }

            Pending pending = entry.getValue();
            if (player.position().distanceToSqr(pending.start) > MOVE_TOLERANCE_SQR) {
                player.sendSystemMessage(Component.translatable("message.pannotiacompanion.cancelled_moved").withStyle(net.minecraft.ChatFormatting.RED));
                continue;
            }

            Pending next = pending.tick();
            if (next.ticksLeft <= 0) {
                complete(player, next);
            } else {
                updated.put(entry.getKey(), next);
            }
        }

        PENDING.putAll(updated);
    }
}
