package com.chimericdream.pannotiacompanion.fabric.loot;

import com.chimericdream.pannotiacompanion.loot.PannotiaLootTables;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class PannotiaFabricLoot {
    private static final String PLAYER_HEAD_TABLE = Blocks.PLAYER_HEAD.getLootTable().orElseThrow().identifier().toString();

    private PannotiaFabricLoot() {
    }

    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Only touch built-in (vanilla) tables, never data pack tables.
            if (!source.isBuiltin()) {
                return;
            }

            LootPool.Builder addition = PannotiaLootTables.additionFor(key);
            if (addition != null) {
                tableBuilder.pool(addition.build());
            }

            // Vanilla only keeps a placed head's profile, sound and custom name when broken; keep every component
            // (e.g. the item name of mini blocks and mob heads) by adding a function to the existing pool.
            if (PLAYER_HEAD_TABLE.equals(key.identifier().toString())) {
                tableBuilder.modifyPools(pool -> pool.apply(
                    CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                ));
            }
        });
    }
}
