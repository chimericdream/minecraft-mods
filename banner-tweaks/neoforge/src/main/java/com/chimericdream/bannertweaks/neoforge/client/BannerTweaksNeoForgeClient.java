package com.chimericdream.bannertweaks.neoforge.client;

import com.chimericdream.bannertweaks.ModInfo;
import com.chimericdream.bannertweaks.network.ServerNetworking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class BannerTweaksNeoForgeClient {
    // Otherwise the server's pushed limit permanently overwrites the client's own configured value
    // in BannerTweaksConfig - restore it once this connection ends.
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ServerNetworking.restoreClientLimit();
    }
}
