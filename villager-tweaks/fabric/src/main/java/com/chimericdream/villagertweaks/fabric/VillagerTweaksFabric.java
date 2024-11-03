package com.chimericdream.villagertweaks.fabric;

import com.chimericdream.villagertweaks.VillagerTweaksMod;
import net.fabricmc.api.ModInitializer;

public final class VillagerTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        VillagerTweaksMod.init();
    }
}
