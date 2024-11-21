package com.chimericdream.shulkerstuff.fabric;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.loot.SSLootTableModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public final class ShulkerStuffFabric implements ModInitializer {
    private static final SSLootTableModifier lootTableModifier = new SSLootTableModifier();

    @Override
    public void onInitialize() {
        ShulkerStuffMod.init();

        LootTableEvents.MODIFY.register((id, tableBuilder, source, wrapperLookup) -> {
            // Only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            if (!source.isBuiltin()) {
                return;
            }

            lootTableModifier.modifyLootTables(id, tableBuilder, wrapperLookup);
        });
    }
}
