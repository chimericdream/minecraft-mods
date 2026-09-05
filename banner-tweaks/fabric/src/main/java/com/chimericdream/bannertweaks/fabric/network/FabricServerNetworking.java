package com.chimericdream.bannertweaks.fabric.network;

import com.chimericdream.bannertweaks.network.ServerNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricServerNetworking {
    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(ServerNetworking.BannerLayerLimitPayload.ID, ServerNetworking.BannerLayerLimitPayload.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(
            ServerNetworking.BannerLayerLimitPayload.ID,
            (payload, context) -> ServerNetworking.applyServerLimit(payload.getLimit())
        );

        // Otherwise the server's pushed limit permanently overwrites the client's own configured
        // value in BannerTweaksConfig - restore it once this connection ends.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ServerNetworking.restoreClientLimit());
    }
}
