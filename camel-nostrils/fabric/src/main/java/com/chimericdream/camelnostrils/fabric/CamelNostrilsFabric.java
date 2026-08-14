package com.chimericdream.camelnostrils.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.camelnostrils.CamelNostrilsMod;

public final class CamelNostrilsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CamelNostrilsMod.init();
    }
}
