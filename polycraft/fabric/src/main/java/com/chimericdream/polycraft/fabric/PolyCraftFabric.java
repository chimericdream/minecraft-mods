package com.chimericdream.polycraft.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.polycraft.PolyCraftMod;

public final class PolyCraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PolyCraftMod.init();
    }
}
