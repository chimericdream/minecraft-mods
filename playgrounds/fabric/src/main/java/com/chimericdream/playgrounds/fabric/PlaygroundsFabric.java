package com.chimericdream.playgrounds.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.playgrounds.PlaygroundsMod;

public final class PlaygroundsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlaygroundsMod.init();
    }
}
