package com.chimericdream.pannotiacompanion.fabric.loot;

import com.chimericdream.pannotiacompanion.PannotiaCompanionMod;
import com.chimericdream.pannotiacompanion.loot.PannotiaLootTables;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.storage.loot.LootTable;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class PannotiaFabricLoot {
    private static final String PLAYER_HEAD_TABLE = Blocks.PLAYER_HEAD.getLootTable().orElseThrow().identifier().toString();

    private PannotiaFabricLoot() {
    }

    private static final String ANCIENT_DEBRIS_TABLE = Blocks.ANCIENT_DEBRIS.getLootTable().orElseThrow().identifier().toString();

    public static void init() {
        // Ancient debris is the one deliberate replacement: it drops netherite scrap instead of debris unless mined
        // with Silk Touch. Using REPLACE (not clearing pools in MODIFY) means other mods' MODIFY additions still apply.
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            if (!source.isBuiltin() || !ANCIENT_DEBRIS_TABLE.equals(key.identifier().toString())) {
                return null;
            }

            try (var stream = PannotiaFabricLoot.class.getResourceAsStream("/pannotia_replacements/ancient_debris.json")) {
                var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                return LootTable.DIRECT_CODEC
                    .parse(RegistryOps.create(JsonOps.INSTANCE, registries), json)
                    .getOrThrow();
            } catch (Exception e) {
                PannotiaCompanionMod.LOGGER.error("Could not load the ancient debris loot table override; keeping vanilla's", e);
                return null;
            }
        });

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
