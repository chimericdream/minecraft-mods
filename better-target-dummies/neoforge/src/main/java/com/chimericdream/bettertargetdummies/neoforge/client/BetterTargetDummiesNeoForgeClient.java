package com.chimericdream.bettertargetdummies.neoforge.client;

import com.chimericdream.bettertargetdummies.ModInfo;
import com.chimericdream.bettertargetdummies.client.BetterTargetDummiesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class BetterTargetDummiesNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BetterTargetDummiesClient.onInitializeClient();
    }
}
