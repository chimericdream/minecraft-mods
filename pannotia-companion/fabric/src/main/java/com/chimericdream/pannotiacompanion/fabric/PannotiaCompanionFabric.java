package com.chimericdream.pannotiacompanion.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.pannotiacompanion.PannotiaCompanionMod;
import com.chimericdream.pannotiacompanion.fabric.loot.PannotiaFabricLoot;

public final class PannotiaCompanionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PannotiaCompanionMod.init();
        PannotiaFabricLoot.init();
    }
}
