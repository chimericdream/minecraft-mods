package com.chimericdream.minekea.fabric;

import com.chimericdream.minekea.MinekeaMod;
import net.fabricmc.api.ModInitializer;

public final class MinekeaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MinekeaMod.init();

        MinekeaMod.LOGGER.info("Initializing Fabric server networking");
//        FabricServerNetworking.init();

        MinekeaMod.initVillagerPois();
    }
}
