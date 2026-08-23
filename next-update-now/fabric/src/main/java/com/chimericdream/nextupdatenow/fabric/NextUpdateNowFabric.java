package com.chimericdream.nextupdatenow.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.nextupdatenow.NextUpdateNowMod;

public final class NextUpdateNowFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NextUpdateNowMod.init();
    }
}
