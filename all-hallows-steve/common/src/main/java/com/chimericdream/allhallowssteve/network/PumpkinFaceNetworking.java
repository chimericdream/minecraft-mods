package com.chimericdream.allhallowssteve.network;

import com.chimericdream.allhallowssteve.advancement.ModTriggers;
import com.chimericdream.allhallowssteve.advancement.PumpkinEvent;
import com.chimericdream.allhallowssteve.client.ClientPumpkinFaces;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaceData;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Map;
import java.util.UUID;

/**
 * Keeps every client's idea of "which face is each player's worn pumpkin turned to" in step with the
 * server. The server owns the truth ({@link PumpkinFaceData}); a player's cycle request flips it and the
 * result goes to every player. Faces aren't tied to who is nearby: the packet is tiny, a client only
 * looks a face up when it draws a pumpkin on that player's head, and an entry for a far-away player is
 * never used.
 * <p>
 * Dispenser-equipped pumpkins and armor stands have no owner, so they never appear here and always show
 * the default face.
 */
public final class PumpkinFaceNetworking {
    private PumpkinFaceNetworking() {
    }

    /**
     * Registers both packets. One {@code registerReceiver} per payload, from common init, registers the
     * payload type on both sides; calling it again from client-only code would double-register it.
     */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            CyclePumpkinFacePayload.ID,
            CyclePumpkinFacePayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> cycle(player));
                }
            }
        );

        NetworkManager.registerReceiver(
            NetworkManager.s2c(),
            PumpkinFacePayload.ID,
            PumpkinFacePayload.CODEC,
            (payload, context) -> context.queue(() -> ClientPumpkinFaces.set(payload.player(), payload.face()))
        );

        PlayerEvent.PLAYER_JOIN.register(PumpkinFaceNetworking::onJoin);
    }

    /** Turns {@code player}'s worn pumpkin to its next face, if they're wearing one, and tells everyone. */
    public static void cycle(ServerPlayer player) {
        if (!PumpkinFaces.isWearable(player.getItemBySlot(EquipmentSlot.HEAD))) {
            return;
        }

        MinecraftServer server = player.level().getServer();
        Direction face = PumpkinFaceData.get(server).cycle(player.getUUID());
        ModTriggers.fire(player, PumpkinEvent.TURNED);

        NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), new PumpkinFacePayload(player.getUUID(), face));
    }

    /** A joining player learns everyone's face, and everyone learns the joiner's. */
    private static void onJoin(ServerPlayer joiner) {
        MinecraftServer server = joiner.level().getServer();
        PumpkinFaceData data = PumpkinFaceData.get(server);

        for (Map.Entry<UUID, Direction> entry : data.all().entrySet()) {
            NetworkManager.sendToPlayer(joiner, new PumpkinFacePayload(entry.getKey(), entry.getValue()));
        }

        Direction own = data.get(joiner.getUUID());
        if (own != PumpkinFaces.DEFAULT) {
            NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), new PumpkinFacePayload(joiner.getUUID(), own));
        }
    }
}
