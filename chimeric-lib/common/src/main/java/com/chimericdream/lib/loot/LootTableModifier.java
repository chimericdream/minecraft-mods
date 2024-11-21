package com.chimericdream.lib.loot;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

abstract public class LootTableModifier {
    abstract protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, RegistryWrapper.WrapperLookup wrapperLookup);

    protected LootPool.Builder makeWeightedItem(Item item, int chance) {
        LootPool.Builder builder = LootPool.builder()
            .with(ItemEntry.builder(item));

        if (chance == 1) {
            return builder;
        }

        return builder.with(ItemEntry.builder(Items.AIR).weight(chance - 1));
    }

    public List<LootPool.Builder> generatePoolBuilders(Identifier id, RegistryWrapper.WrapperLookup wrapperLookup) {
        List<LootPool.Builder> poolBuilders = new ArrayList<>();

        checkVanillaLootTables(id, poolBuilders, wrapperLookup);

        return poolBuilders;
    }

    public void modifyLootTables(RegistryKey<LootTable> id, LootTable.Builder tableBuilder, RegistryWrapper.WrapperLookup wrapperLookup) {
        modifyLootTables(id.getValue(), tableBuilder, wrapperLookup);
    }

    public void modifyLootTables(Identifier id, LootTable.Builder tableBuilder, RegistryWrapper.WrapperLookup wrapperLookup) {
        List<LootPool.Builder> poolBuilders = generatePoolBuilders(id, wrapperLookup);

        for (LootPool.Builder builder : poolBuilders) {
            tableBuilder.pool(builder);
        }
    }
}
