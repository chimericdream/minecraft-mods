package com.chimericdream.template.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.template.TemplateMod;

public final class TemplateFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TemplateMod.init();
    }
}
