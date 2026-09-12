package com.chimericdream.allhallowssteve.loot;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.lib.loot.LootTableModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.List;

/**
 * Adds the loot-only carving stencils (no crafting recipe - see
 * {@code PumpkinStencilItemDataGenerator}) to the vanilla structure chests they're themed after.
 */
public class AHSLootTableModifier extends LootTableModifier {
    private static final Identifier[] VILLAGE_HOUSE_LOOT_TABLES = {
        BuiltInLootTables.VILLAGE_DESERT_HOUSE.identifier(),
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE.identifier(),
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.identifier(),
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE.identifier(),
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE.identifier(),
    };

    private static final Identifier[] STRONGHOLD_LOOT_TABLES = {
        BuiltInLootTables.STRONGHOLD_LIBRARY.identifier(),
        BuiltInLootTables.STRONGHOLD_CORRIDOR.identifier(),
        BuiltInLootTables.STRONGHOLD_CROSSING.identifier(),
    };

    @Override
    protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, HolderLookup.Provider wrapperLookup) {
        if (contains(VILLAGE_HOUSE_LOOT_TABLES, id)) {
            poolBuilders.add(makeWeightedItem(ModItems.HEART_STENCIL.get(), 100));
        }

        if (contains(STRONGHOLD_LOOT_TABLES, id)) {
            poolBuilders.add(makeWeightedItem(ModItems.JIGSAW_STENCIL.get(), 4));
        }

        if (BuiltInLootTables.WOODLAND_MANSION.identifier().equals(id)) {
            poolBuilders.add(makeThreeQuartersChance(ModItems.JIGSAW_STENCIL.get()));
        }

        if (BuiltInLootTables.SIMPLE_DUNGEON.identifier().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.SPAWNER_STENCIL.get(), 20));
        }

        if (BuiltInLootTables.ANCIENT_CITY.identifier().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.STRUCTURE_BLOCK_STENCIL.get(), 20));
        }

        if (BuiltInLootTables.END_CITY_TREASURE.identifier().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.STRUCTURE_BLOCK_STENCIL.get(), 20));
        }
    }

    /** {@code makeWeightedItem} can only express a 1-in-N chance; the mansion's jigsaw drop is 75%. */
    private static LootPool.Builder makeThreeQuartersChance(Item item) {
        return LootPool.lootPool()
            .add(LootItem.lootTableItem(item).setWeight(3))
            .add(LootItem.lootTableItem(Items.AIR).setWeight(1));
    }

    private static boolean contains(Identifier[] ids, Identifier id) {
        for (Identifier candidate : ids) {
            if (candidate.equals(id)) {
                return true;
            }
        }

        return false;
    }
}
