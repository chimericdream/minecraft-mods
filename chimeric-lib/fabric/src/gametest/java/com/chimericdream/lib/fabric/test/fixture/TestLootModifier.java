package com.chimericdream.lib.fabric.test.fixture;

import com.chimericdream.lib.loot.LootTableModifier;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;

/**
 * A test {@link LootTableModifier} that injects a single guaranteed marker item ({@link Items#DIAMOND})
 * into exactly one targeted vanilla loot table id, and leaves every other table untouched. Used to
 * validate the modifier's routing and pool generation.
 */
public class TestLootModifier extends LootTableModifier {
    public static final Identifier TARGET = Identifier.withDefaultNamespace("blocks/stone");
    public static final Item MARKER = Items.DIAMOND;

    @Override
    protected void checkVanillaLootTables(Identifier id, List<LootPool.Builder> poolBuilders, HolderLookup.Provider wrapperLookup) {
        if (id.equals(TARGET)) {
            // chance == 1 -> a single, guaranteed marker entry, so a roll always yields it.
            poolBuilders.add(makeWeightedItem(MARKER, 1));
        }
    }
}
