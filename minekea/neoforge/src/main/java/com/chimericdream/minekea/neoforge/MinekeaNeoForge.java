package com.chimericdream.minekea.neoforge;

import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.client.MinekeaClient;
import com.chimericdream.minekea.neoforge.client.MinekeaNeoForgeClient;
import com.chimericdream.minekea.neoforge.network.NeoForgeServerNetworking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ModInfo.MOD_ID)
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public final class MinekeaNeoForge {
    public MinekeaNeoForge() {
        MinekeaMod.init();
        NeoForgeServerNetworking.init();

        // Must run before construction finishes: architectury's own RegisterRenderers listener fires on
        // architectury's mod bus before ours, so any lifecycle event hook here is already too late.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            MinekeaClient.registerEntityRenderers();

            NeoForge.EVENT_BUS.addListener(MinekeaNeoForgeClient::onClientTick);
        }
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        MinekeaMod.postInit();
        MinekeaMod.initVillagerPois();
    }
}
