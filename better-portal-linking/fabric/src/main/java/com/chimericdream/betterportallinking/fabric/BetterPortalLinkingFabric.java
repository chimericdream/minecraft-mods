package com.chimericdream.betterportallinking.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.betterportallinking.BetterPortalLinkingMod;

public final class BetterPortalLinkingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterPortalLinkingMod.init();
    }
}
