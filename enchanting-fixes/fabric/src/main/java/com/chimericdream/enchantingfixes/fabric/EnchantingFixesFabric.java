package com.chimericdream.enchantingfixes.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.enchantingfixes.EnchantingFixesMod;

public final class EnchantingFixesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantingFixesMod.init();
    }
}
