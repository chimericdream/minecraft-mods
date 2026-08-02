package com.chimericdream.sneakytweaks.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.sneakytweaks.SneakyTweaksMod;

public final class SneakyTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SneakyTweaksMod.init();
    }
}
