package com.chimericdream.shulkerstuff.loot;

import com.chimericdream.lib.loot.LootTableModifier;
import com.chimericdream.shulkerstuff.config.ShulkerStuffConfig;
import com.chimericdream.shulkerstuff.item.ModItems;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;

public class SSLootTableModifier extends LootTableModifier {
    protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, RegistryWrapper.WrapperLookup wrapperLookup) {
        ShulkerStuffConfig config = ShulkerStuffConfig.HANDLER.instance();

        if (LootTables.BASTION_TREASURE_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.PLATED_SHULKER_UPGRADE.get(), 12));
        }

        if (LootTables.ANCIENT_CITY_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.HARDENED_SHULKER_UPGRADE.get(), 25));
        }

        if (LootTables.END_CITY_TREASURE_CHEST.getValue().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.HARDENED_SHULKER_UPGRADE.get(), 15));
        }
    }
}
