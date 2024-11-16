package com.chimericdream.shulkerstuff.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;

public final class ShulkerStuffFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ShulkerStuffMod.init();
    }
}
