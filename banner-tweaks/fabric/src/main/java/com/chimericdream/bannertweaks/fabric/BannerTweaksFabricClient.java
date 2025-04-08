package com.chimericdream.bannertweaks.fabric;

import com.chimericdream.bannertweaks.BannerTweaksMod;
import com.chimericdream.bannertweaks.fabric.network.FabricServerNetworking;
import net.fabricmc.api.ClientModInitializer;

public class BannerTweaksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BannerTweaksMod.LOGGER.info("Initializing Fabric client networking");
        FabricServerNetworking.initClient();
    }
}
