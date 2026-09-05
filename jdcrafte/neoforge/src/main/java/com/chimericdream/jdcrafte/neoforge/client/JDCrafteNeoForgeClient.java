package com.chimericdream.jdcrafte.neoforge.client;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.jdcrafte.client.JDCrafteClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = JDCrafteMod.MOD_ID, value = Dist.CLIENT)
public class JDCrafteNeoForgeClient {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        JDCrafteClient.registerBlockEntityRenderers();
    }
}
