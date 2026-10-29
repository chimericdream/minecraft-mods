package com.chimericdream.rulesforthee.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.rulesforthee.RulesForTheeMod;

public final class RulesForTheeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RulesForTheeMod.init();
    }
}
