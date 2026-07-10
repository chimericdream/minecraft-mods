package com.chimericdream.lib.loot;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;

abstract public class LootTableModifier {
    abstract protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, HolderLookup.Provider wrapperLookup);

    protected LootPool.Builder makeWeightedItem(Item item, int chance) {
        LootPool.Builder builder = LootPool.lootPool()
            .add(LootItem.lootTableItem(item));

        if (chance == 1) {
            return builder;
        }

        return builder.add(LootItem.lootTableItem(Items.AIR).setWeight(chance - 1));
    }

    public List<LootPool.Builder> generatePoolBuilders(Identifier id, HolderLookup.Provider wrapperLookup) {
        List<LootPool.Builder> poolBuilders = new ArrayList<>();

        checkVanillaLootTables(id, poolBuilders, wrapperLookup);

        return poolBuilders;
    }

    public void modifyLootTables(ResourceKey<LootTable> id, LootTable.Builder tableBuilder, HolderLookup.Provider wrapperLookup) {
        modifyLootTables(id.identifier(), tableBuilder, wrapperLookup);
    }

    public void modifyLootTables(Identifier id, LootTable.Builder tableBuilder, HolderLookup.Provider wrapperLookup) {
        List<LootPool.Builder> poolBuilders = generatePoolBuilders(id, wrapperLookup);

        for (LootPool.Builder builder : poolBuilders) {
            tableBuilder.withPool(builder);
        }
    }
}
