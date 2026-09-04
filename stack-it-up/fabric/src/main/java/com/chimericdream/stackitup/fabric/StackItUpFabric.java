package com.chimericdream.stackitup.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.stackitup.StackItUpMod;

public final class StackItUpFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        StackItUpMod.init();
    }
}
