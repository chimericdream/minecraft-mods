package com.chimericdream.allhallowssteve.neoforge.client;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.client.AllHallowsSteveClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class AllHallowsSteveNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        AllHallowsSteveClient.onInitializeClient();
    }
}
