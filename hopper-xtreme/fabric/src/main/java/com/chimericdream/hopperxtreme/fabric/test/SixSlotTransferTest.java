package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

@SuppressWarnings("unused")
public class SixSlotTransferTest {
    @GameTest(structure = "hopperxtreme:transfer_speed/golden_hopper", maxTicks = 28)
    public void goldenHopperItemTransfer(GameTestHelper context) {
        runSixSlotTransferTest(context, 4);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/diamond_hopper")
    public void diamondHopperItemTransfer(GameTestHelper context) {
        runSixSlotTransferTest(context, 2);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/netherite_hopper")
    public void netheriteHopperItemTransfer(GameTestHelper context) {
        runSixSlotTransferTest(context, 1);
    }

    private void runSixSlotTransferTest(GameTestHelper context, int singleItemTickTime) {
        BlockPos redstoneBlockPos = new BlockPos(2, 1, 1);
        BlockPos topChestPos = new BlockPos(1, 2, 1);
        BlockPos hopperPos = new BlockPos(1, 1, 1);
        BlockPos bottomChestPos = new BlockPos(1, 0, 1);

        BaseContainerBlockEntity topChest = getChestAt(context, topChestPos);
        topChest.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        topChest.setItem(1, new ItemStack(Items.OAK_WOOD, 1));
        topChest.setItem(2, new ItemStack(Items.STRIPPED_OAK_LOG, 1));
        topChest.setItem(3, new ItemStack(Items.STRIPPED_OAK_WOOD, 1));
        topChest.setItem(4, new ItemStack(Items.OAK_PLANKS, 1));
        topChest.setItem(5, new ItemStack(Items.OAK_STAIRS, 1));

        context.destroyBlock(redstoneBlockPos);

        context.runAfterDelay((long) 7 * singleItemTickTime, () -> {
            context.setBlock(redstoneBlockPos, Blocks.REDSTONE_BLOCK.defaultBlockState());

            XtremeHopperBlockEntity hopper = getHopperAt(context, hopperPos);

            ItemStack hopperSlot0 = hopper.getItem(0);
            if (!hopperSlot0.isEmpty()) {
                context.fail(Component.literal("Expected the hopper to be empty, but found " + hopperSlot0), hopperPos);
            }

            BaseContainerBlockEntity bottomChest = getChestAt(context, bottomChestPos);

            ItemStack bottomSlot0 = bottomChest.getItem(0);
            ItemStack bottomSlot1 = bottomChest.getItem(1);
            ItemStack bottomSlot2 = bottomChest.getItem(2);
            ItemStack bottomSlot3 = bottomChest.getItem(3);
            ItemStack bottomSlot4 = bottomChest.getItem(4);
            ItemStack bottomSlot5 = bottomChest.getItem(5);

            if (!bottomSlot0.is(Items.OAK_LOG) || bottomSlot0.getCount() != 1) {
                context.fail(Component.literal("Expected 1 oak log in slot 0 of the bottom chest, but found " + bottomSlot0), bottomChestPos);
            }
            if (!bottomSlot1.is(Items.OAK_WOOD) || bottomSlot1.getCount() != 1) {
                context.fail(Component.literal("Expected 1 oak wood in slot 1 of the bottom chest, but found " + bottomSlot1), bottomChestPos);
            }
            if (!bottomSlot2.is(Items.STRIPPED_OAK_LOG) || bottomSlot2.getCount() != 1) {
                context.fail(Component.literal("Expected 1 stripped oak log in slot 2 of the bottom chest, but found " + bottomSlot2), bottomChestPos);
            }
            if (!bottomSlot3.is(Items.STRIPPED_OAK_WOOD) || bottomSlot3.getCount() != 1) {
                context.fail(Component.literal("Expected 1 stripped oak wood in slot 3 of the bottom chest, but found " + bottomSlot3), bottomChestPos);
            }
            if (!bottomSlot4.is(Items.OAK_PLANKS) || bottomSlot4.getCount() != 1) {
                context.fail(Component.literal("Expected 1 oak planks in slot 4 of the bottom chest, but found " + bottomSlot4), bottomChestPos);
            }
            if (!bottomSlot5.is(Items.OAK_STAIRS) || bottomSlot5.getCount() != 1) {
                context.fail(Component.literal("Expected 1 oak stairs in slot 5 of the bottom chest, but found " + bottomSlot5), bottomChestPos);
            }

            context.succeed();
        });
    }

    private XtremeHopperBlockEntity getHopperAt(GameTestHelper context, BlockPos pos) {
        return context.getBlockEntity(pos, XtremeHopperBlockEntity.class);
    }

    private BaseContainerBlockEntity getChestAt(GameTestHelper context, BlockPos pos) {
        return context.getBlockEntity(pos, BaseContainerBlockEntity.class);
    }
}
