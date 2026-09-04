package com.chimericdream.effectivegear.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.effectivegear.EffectiveGearMod;
import com.chimericdream.effectivegear.fabric.network.FabricServerNetworking;

public final class EffectiveGearFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EffectiveGearMod.init();
        FabricServerNetworking.init();
    }
}
