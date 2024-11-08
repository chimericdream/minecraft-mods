package com.chimericdream.blacklight.fabric;

import com.chimericdream.blacklight.BlacklightMod;
import net.fabricmc.api.ModInitializer;

public final class BlacklightFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BlacklightMod.init();
    }
}
