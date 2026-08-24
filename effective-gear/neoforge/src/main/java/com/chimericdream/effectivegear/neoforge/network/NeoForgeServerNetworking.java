package com.chimericdream.effectivegear.neoforge.network;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.network.ServerNetworking;
import com.chimericdream.effectivegear.network.UseAbilityPayload;
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
        registrar.playToServer(UseAbilityPayload.ID, UseAbilityPayload.CODEC, NeoForgeServerNetworking::receiveUseAbilityPacket);
    }

    public static void receiveUseAbilityPacket(final UseAbilityPayload payload, final IPayloadContext context) {
        Player player = context.player();
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.level().getServer();
            handleUseAbilityPacket(server, serverPlayer);
        }
    }
}
