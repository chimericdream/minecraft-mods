package com.chimericdream.bettertargetdummies.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.bettertargetdummies.BetterTargetDummiesMod;

public final class BetterTargetDummiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterTargetDummiesMod.init();
    }
}
