package com.chimericdream.pannotiacompanion.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.pannotiacompanion.PannotiaCompanionMod;

public final class PannotiaCompanionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PannotiaCompanionMod.init();
    }
}
