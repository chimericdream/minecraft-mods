package com.chimericdream.camelnostrils.neoforge;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.client.CamelNostrilsClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(ModInfo.MOD_ID)
public final class CamelNostrilsNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CamelNostrilsClient.registerEntityRenderers();
    }
}
