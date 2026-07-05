package com.chimericdream.minekea.neoforge.network;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.network.CyclePainterColorPayload;
import com.chimericdream.minekea.network.ServerNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoForgeServerNetworking extends ServerNetworking {
    public static void init() {
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
            CyclePainterColorPayload.ID,
            CyclePainterColorPayload.CODEC,
            NeoForgeServerNetworking::receiveCyclePainterColorPacket
        );
    }

    public static void receiveCyclePainterColorPacket(final CyclePainterColorPayload payload, final IPayloadContext context) {
        MinecraftServer server = context.player().level().getServer();
        Player player = context.player();

        if (player instanceof ServerPlayer) {
            handleCyclePainterColorPacket(server, (ServerPlayer) player);
        }
    }
}
