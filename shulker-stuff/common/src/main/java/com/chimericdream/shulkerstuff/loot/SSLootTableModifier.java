package com.chimericdream.shulkerstuff.loot;

import com.chimericdream.lib.loot.LootTableModifier;
import com.chimericdream.shulkerstuff.config.ShulkerStuffConfig;
import com.chimericdream.shulkerstuff.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

public class SSLootTableModifier extends LootTableModifier {
    protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, HolderLookup.Provider wrapperLookup) {
        ShulkerStuffConfig config = ShulkerStuffConfig.HANDLER.instance();

        if (BuiltInLootTables.BASTION_TREASURE.identifier().equals(id)) {
            poolBuilders.add(makeWeightedItem(ModItems.PLATED_SHULKER_UPGRADE.get(), 12));
        }
    }
}
