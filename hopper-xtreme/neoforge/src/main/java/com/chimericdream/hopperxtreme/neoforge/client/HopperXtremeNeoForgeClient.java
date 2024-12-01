package com.chimericdream.hopperxtreme.neoforge.client;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.HopperXtremeClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class HopperXtremeNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        HopperXtremeClient.onInitializeClient();
    }
}
