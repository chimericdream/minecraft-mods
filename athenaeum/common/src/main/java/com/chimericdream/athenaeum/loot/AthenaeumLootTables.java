package com.chimericdream.athenaeum.loot;

import dev.architectury.event.events.common.LootEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class AthenaeumLootTables {
    private static LootPool.Builder makeBuilder(int minRolls, int maxRolls, int chance) {
        LootPoolEntryContainer.Builder<?> itemBuilder = LootItem
            .lootTableItem(Items.WRITTEN_BOOK)
            .apply(GetRandomBookFunction.builder());

        LootPool.Builder builder = LootPool.lootPool()
            .add(itemBuilder)
            .setRolls(UniformGenerator.between(minRolls, maxRolls));

        if (chance == 1) {
            return builder;
        }

        return builder.add(EmptyLootItem.emptyItem().setWeight(chance - 1));
    }

    private static void checkVanillaLootTables(
        ResourceKey<LootTable> key,
        List<LootPool.Builder> lootPools
    ) {
        if (BuiltInLootTables.STRONGHOLD_LIBRARY.equals(key)) {
            lootPools.add(makeBuilder(2, 6, 3));
        }

        if (BuiltInLootTables.WOODLAND_MANSION.equals(key)) {
            lootPools.add(makeBuilder(1, 4, 4));
        }
    }

    public static void init() {
        LootEvent.MODIFY_LOOT_TABLE.register((key, context, builtin) -> {
            if (!builtin) {
                return;
            }

            List<LootPool.Builder> lootPools = new ArrayList<>();
            checkVanillaLootTables(key, lootPools);

            for (LootPool.Builder builder : lootPools) {
                context.addPool(builder);
            }
        });
    }
}
