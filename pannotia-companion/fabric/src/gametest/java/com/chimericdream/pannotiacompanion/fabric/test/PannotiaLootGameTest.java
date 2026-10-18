package com.chimericdream.pannotiacompanion.fabric.test;

import com.chimericdream.pannotiacompanion.ModInfo;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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

    @GameTest
    public void ancientDebrisDropsScrapWithoutSilkTouch(GameTestHelper context) {
        var level = context.getLevel();
        LootTable table = level.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE)
            .getOrThrow(Blocks.ANCIENT_DEBRIS.getLootTable().orElseThrow()).value();
        LootParams params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
            .withParameter(LootContextParams.BLOCK_STATE, Blocks.ANCIENT_DEBRIS.defaultBlockState())
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
            .create(LootContextParamSets.BLOCK);

        List<ItemStack> drops = table.getRandomItems(params);
        if (drops.stream().noneMatch(stack -> stack.is(Items.NETHERITE_SCRAP))) {
            throw context.assertionException("Expected netherite scrap, got " + drops);
        }
        if (drops.stream().anyMatch(stack -> stack.is(Items.ANCIENT_DEBRIS))) {
            throw context.assertionException("Ancient debris should not drop without Silk Touch: " + drops);
        }

        context.succeed();
    }
}
