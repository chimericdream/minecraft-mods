package com.chimericdream.lib.fabric.test;

import com.chimericdream.lib.fabric.test.fixture.TestContainerBlockEntity;
import com.chimericdream.lib.fabric.test.fixture.TestFixtures;
import com.chimericdream.lib.testkit.gametest.GameTestContainers;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Exercises the library's {@code ImplementedInventory} contract through a fixture container block
 * entity: an NBT save/load round-trip preserves its contents, and a vanilla hopper can insert into it
 * (proving the {@code Container} contract behaves for real game systems).
 */
@SuppressWarnings("unused")
public class ImplementedInventoryGameTest {
    private static final BlockPos CONTAINER = new BlockPos(2, 2, 2);

    @GameTest
    public void nbtRoundTripPreservesContents(GameTestHelper context) {
        context.setBlock(CONTAINER, TestFixtures.TEST_CONTAINER_BLOCK.get());
        TestContainerBlockEntity be = context.getBlockEntity(CONTAINER, TestContainerBlockEntity.class);

        be.setItem(0, new ItemStack(Items.DIAMOND, 5));
        be.setItem(TestContainerBlockEntity.SIZE - 1, new ItemStack(Items.EMERALD, 2));
        GameTestContainers.assertSlot(context, be, 0, Items.DIAMOND, 5);
        GameTestContainers.assertSlot(context, be, TestContainerBlockEntity.SIZE - 1, Items.EMERALD, 2);

        HolderLookup.Provider provider = context.getLevel().registryAccess();
        CompoundTag tag = be.saveWithFullMetadata(provider);
        BlockEntity reloaded = BlockEntity.loadStatic(context.absolutePos(CONTAINER), be.getBlockState(), tag, provider);

        context.assertTrue(reloaded instanceof TestContainerBlockEntity, "the reloaded block entity should keep its type");
        TestContainerBlockEntity restored = (TestContainerBlockEntity) reloaded;
        GameTestContainers.assertSlot(context, restored, 0, Items.DIAMOND, 5);
        GameTestContainers.assertSlot(context, restored, TestContainerBlockEntity.SIZE - 1, Items.EMERALD, 2);

        context.succeed();
    }

    @GameTest(maxTicks = 120)
    public void vanillaHopperFeedsTheContainer(GameTestHelper context) {
        BlockPos hopperPos = CONTAINER.above();
        context.setBlock(CONTAINER, TestFixtures.TEST_CONTAINER_BLOCK.get());
        context.setBlock(hopperPos, Blocks.HOPPER); // default facing is DOWN, into the container

        HopperBlockEntity hopper = context.getBlockEntity(hopperPos, HopperBlockEntity.class);
        hopper.setItem(0, new ItemStack(Items.DIAMOND, 3));

        context.succeedWhen(() -> {
            TestContainerBlockEntity be = context.getBlockEntity(CONTAINER, TestContainerBlockEntity.class);
            GameTestContainers.assertSlot(context, be, 0, Items.DIAMOND, 3);
        });
    }
}
