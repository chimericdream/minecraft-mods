package com.chimericdream.stackitup.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import com.chimericdream.stackitup.ModInfo;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class StackItUpNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Network receiver registration happens in StackItUpMod.init() (common init), which
        // Architectury's NetworkManager handles correctly on both client and dedicated server -
        // nothing client-only needed here currently.
    }
}
