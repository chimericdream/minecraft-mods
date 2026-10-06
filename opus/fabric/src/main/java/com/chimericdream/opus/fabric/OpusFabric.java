package com.chimericdream.opus.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.opus.OpusMod;

public final class OpusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OpusMod.init();
    }
}
