package com.chimericdream.butwhatabout.neoforge;

import com.chimericdream.butwhatabout.ModInfo;
import com.chimericdream.butwhatabout.client.ButWhatAboutClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class ButWhatAboutNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ButWhatAboutClient.onInitializeClient();
    }
}
