package com.chimericdream.allhallowssteve.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import com.chimericdream.allhallowssteve.AllHallowsSteveMod;
import com.chimericdream.allhallowssteve.loot.AHSLootTableModifier;

public final class AllHallowsSteveFabric implements ModInitializer {
    private static final AHSLootTableModifier LOOT_TABLE_MODIFIER = new AHSLootTableModifier();

    @Override
    public void onInitialize() {
        AllHallowsSteveMod.init();

        LootTableEvents.MODIFY.register((id, tableBuilder, source, wrapperLookup) -> {
            // Only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            if (!source.isBuiltin()) {
                return;
            }

            LOOT_TABLE_MODIFIER.modifyLootTables(id, tableBuilder, wrapperLookup);
        });
    }
}
