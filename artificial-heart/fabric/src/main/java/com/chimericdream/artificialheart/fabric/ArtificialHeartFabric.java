package com.chimericdream.artificialheart.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.artificialheart.ArtificialHeartMod;

public final class ArtificialHeartFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ArtificialHeartMod.init();
    }
}
