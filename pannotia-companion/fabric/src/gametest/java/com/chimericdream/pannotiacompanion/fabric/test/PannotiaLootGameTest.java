package com.chimericdream.pannotiacompanion.fabric.test;

import com.chimericdream.pannotiacompanion.ModInfo;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/**
 * A loot table that fails to parse is silently dropped (only a log line), so walk every table we ship and make
 * sure each one is actually loaded.
 */
@SuppressWarnings("unused")
public class PannotiaLootGameTest {
    @GameTest
    public void shippedLootTablesLoad(GameTestHelper context) {
        List<Identifier> shipped = context.getLevel().getServer().getResourceManager()
            .listResources("loot_table", id -> id.getNamespace().equals(ModInfo.MOD_ID) && id.getPath().endsWith(".json"))
            .keySet().stream()
            .map(id -> Identifier.fromNamespaceAndPath(
                id.getNamespace(),
                id.getPath().substring("loot_table/".length(), id.getPath().length() - ".json".length())
            ))
            .toList();

        var lookup = context.getLevel().getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE);
        List<Identifier> missing = shipped.stream()
            .filter(id -> lookup.get(ResourceKey.<LootTable>create(Registries.LOOT_TABLE, id)).isEmpty())
            .toList();

        if (shipped.isEmpty()) {
            throw context.assertionException("No shipped loot tables were found");
        }
        if (!missing.isEmpty()) {
            throw context.assertionException("Loot tables failed to load: " + missing);
        }

        context.succeed();
    }
}
