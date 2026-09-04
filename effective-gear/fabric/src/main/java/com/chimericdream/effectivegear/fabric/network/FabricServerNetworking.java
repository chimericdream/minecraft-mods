package com.chimericdream.effectivegear.fabric.network;

import com.chimericdream.effectivegear.network.ServerNetworking;
import com.chimericdream.effectivegear.network.UseAbilityPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FabricServerNetworking extends ServerNetworking {
    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(UseAbilityPayload.ID, UseAbilityPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UseAbilityPayload.ID, FabricServerNetworking::receiveUseAbilityPacket);
    }

    private static void receiveUseAbilityPacket(UseAbilityPayload payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.server();
        ServerPlayer player = context.player();

        handleUseAbilityPacket(server, player);
    }
}
