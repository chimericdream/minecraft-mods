package com.chimericdream.lib.fabric.test;

import com.chimericdream.lib.fabric.test.fixture.TestLootModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

/**
 * Validates {@code LootTableModifier}: it injects its pool only into the targeted table id, leaves
 * every other table untouched, and the injected guaranteed pool actually yields the marker item when
 * the modified table is rolled.
 */
@SuppressWarnings("unused")
public class LootTableModifierGameTest {
    @GameTest
    public void injectsMarkerIntoTargetedTableOnly(GameTestHelper context) {
        HolderLookup.Provider provider = context.getLevel().registryAccess();
        TestLootModifier modifier = new TestLootModifier();

        List<LootPool.Builder> targeted = modifier.generatePoolBuilders(TestLootModifier.TARGET, provider);
        context.assertTrue(targeted.size() == 1, "expected exactly one injected pool for the targeted table, got " + targeted.size());

        List<LootPool.Builder> untargeted = modifier.generatePoolBuilders(Identifier.withDefaultNamespace("blocks/dirt"), provider);
        context.assertTrue(untargeted.isEmpty(), "non-targeted tables must be left untouched");

        // Build the modified table and roll it: the guaranteed pool must always yield the marker.
        LootTable.Builder builder = LootTable.lootTable();
        modifier.modifyLootTables(TestLootModifier.TARGET, builder, provider);
        LootTable table = builder.build();

        LootParams params = new LootParams.Builder(context.getLevel()).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> drops = table.getRandomItems(params);

        boolean hasMarker = drops.stream().anyMatch(stack -> stack.is(TestLootModifier.MARKER));
        context.assertTrue(hasMarker, "the rolled, modified table should contain the injected marker; got " + drops);

        context.succeed();
    }
}
