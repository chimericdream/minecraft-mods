package com.chimericdream.hangfromslabs.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.hangfromslabs.HangFromSlabsMod;

public final class HangFromSlabsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HangFromSlabsMod.init();
    }
}
