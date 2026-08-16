package com.chimericdream.toybox.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.toybox.ToyBoxMod;

public final class ToyBoxFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ToyBoxMod.init();
    }
}
