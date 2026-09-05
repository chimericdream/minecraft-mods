package com.chimericdream.camelnostrils.neoforge.client;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.client.CamelNostrilsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class CamelNostrilsNeoForgeClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        CamelNostrilsClient.registerBlockEntityRenderers();
    }
}
