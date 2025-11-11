package com.chimericdream.miniblockmerchants.fabric;

import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

//import com.chimericdream.miniblockmerchants.loot.MMLootTables;

public final class MiniblockMerchantsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MiniblockMerchantsMod.init();

        LootTableEvents.MODIFY.register((id, tableBuilder, source, wrapperLookup) -> {
            // Only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            if (!source.isBuiltin()) {
                return;
            }

//            MMLootTables.modifyLootTables(id, tableBuilder, wrapperLookup);
        });
    }
}
