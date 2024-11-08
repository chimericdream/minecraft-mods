package com.chimericdream.cobblicious.fabric;

import com.chimericdream.cobblicious.CobbliciousMod;
import net.fabricmc.api.ModInitializer;

public final class CobbliciousModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobbliciousMod.init();
    }
}
