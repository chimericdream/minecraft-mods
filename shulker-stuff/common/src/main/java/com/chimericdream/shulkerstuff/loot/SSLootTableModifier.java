package com.chimericdream.shulkerstuff.loot;

import com.chimericdream.lib.loot.LootTableModifier;
import com.chimericdream.shulkerstuff.config.ShulkerStuffConfig;
import com.chimericdream.shulkerstuff.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public class SSLootTableModifier extends LootTableModifier {
    protected void checkVanillaLootTables(ResourceLocation id, List<net.minecraft.world.level.storage.loot.LootPool.Builder> poolBuilders, HolderLookup.Provider wrapperLookup) {
        ShulkerStuffConfig config = ShulkerStuffConfig.HANDLER.instance();

        if (BuiltInLootTables.BASTION_TREASURE.location().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.PLATED_SHULKER_UPGRADE.get(), 12));
        }

        if (BuiltInLootTables.ANCIENT_CITY.location().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.HARDENED_SHULKER_UPGRADE.get(), 25));
        }

        if (BuiltInLootTables.END_CITY_TREASURE.location().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.HARDENED_SHULKER_UPGRADE.get(), 15));
        }
    }
}
