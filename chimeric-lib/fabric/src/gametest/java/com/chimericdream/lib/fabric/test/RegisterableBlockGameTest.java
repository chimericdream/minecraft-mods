package com.chimericdream.lib.fabric.test;

import com.chimericdream.lib.fabric.test.fixture.TestFixtures;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Proves {@code ModRegistryHelper} + {@code registerWithItem}/{@code registerBlockEntity} register
 * everything under the expected ids: a plain block resolves alongside its {@link BlockItem}, and a
 * block that owns a block entity resolves the whole block/item/block-entity trio.
 */
@SuppressWarnings("unused")
public class RegisterableBlockGameTest {
    @GameTest
    public void registerWithItemRegistersBlockAndItem(GameTestHelper context) {
        Identifier id = TestFixtures.REGISTRY_HELPER.makeId("test_block");

        Block block = BuiltInRegistries.BLOCK.getValue(id);
        context.assertTrue(block == TestFixtures.TEST_BLOCK.get(), "block should resolve at " + id);

        Item item = BuiltInRegistries.ITEM.getValue(id);
        context.assertTrue(item instanceof BlockItem blockItem && blockItem.getBlock() == block,
            "a BlockItem pointing at the block should resolve at " + id);

        context.succeed();
    }

    @GameTest
    public void registerBlockEntityResolvesTheWholeTrio(GameTestHelper context) {
        Identifier id = TestFixtures.REGISTRY_HELPER.makeId("test_container");

        Block block = BuiltInRegistries.BLOCK.getValue(id);
        context.assertTrue(block == TestFixtures.TEST_CONTAINER_BLOCK.get(), "container block should resolve at " + id);

        Item item = BuiltInRegistries.ITEM.getValue(id);
        context.assertTrue(item instanceof BlockItem, "a BlockItem should resolve at " + id);

        BlockEntityType<?> beType = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(id);
        context.assertTrue(beType == TestFixtures.TEST_CONTAINER_BE.get(), "the block entity type should resolve at " + id);
        context.assertTrue(block.defaultBlockState().hasBlockEntity(), "the block should carry a block entity");

        context.succeed();
    }
}
