package com.chimericdream.logallthethings.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import com.chimericdream.logallthethings.ModInfo;
import com.chimericdream.logallthethings.client.LogAllTheThingsClient;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public final class LogAllTheThingsNeoForgeClient {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LogAllTheThingsClient.registerBlockEntityRenderers();
    }
}
