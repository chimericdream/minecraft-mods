package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class SixSlotTransferTest {
    @GameTest(structure = "hopperxtreme:transfer_speed/golden_hopper", maxTicks = 28)
    public void goldenHopperItemTransfer(TestContext context) {
        runSixSlotTransferTest(context, 4);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/diamond_hopper")
    public void diamondHopperItemTransfer(TestContext context) {
        runSixSlotTransferTest(context, 2);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/netherite_hopper")
    public void netheriteHopperItemTransfer(TestContext context) {
        runSixSlotTransferTest(context, 1);
    }

    private void runSixSlotTransferTest(TestContext context, int singleItemTickTime) {
        BlockPos redstoneBlockPos = new BlockPos(2, 1, 1);
        BlockPos topChestPos = new BlockPos(1, 2, 1);
        BlockPos hopperPos = new BlockPos(1, 1, 1);
        BlockPos bottomChestPos = new BlockPos(1, 0, 1);

        LockableContainerBlockEntity topChest = getChestAt(context, topChestPos);
        topChest.setStack(0, new ItemStack(Items.OAK_LOG, 1));
        topChest.setStack(1, new ItemStack(Items.OAK_WOOD, 1));
        topChest.setStack(2, new ItemStack(Items.STRIPPED_OAK_LOG, 1));
        topChest.setStack(3, new ItemStack(Items.STRIPPED_OAK_WOOD, 1));
        topChest.setStack(4, new ItemStack(Items.OAK_PLANKS, 1));
        topChest.setStack(5, new ItemStack(Items.OAK_STAIRS, 1));

        context.removeBlock(redstoneBlockPos);

        context.waitAndRun((long) 7 * singleItemTickTime, () -> {
            context.setBlockState(redstoneBlockPos, Blocks.REDSTONE_BLOCK.getDefaultState());

            XtremeHopperBlockEntity hopper = getHopperAt(context, hopperPos);

            ItemStack hopperSlot0 = hopper.getStack(0);
            if (!hopperSlot0.isEmpty()) {
                context.throwPositionedException(Text.of("Expected the hopper to be empty, but found " + hopperSlot0), hopperPos);
            }

            LockableContainerBlockEntity bottomChest = getChestAt(context, bottomChestPos);

            ItemStack bottomSlot0 = bottomChest.getStack(0);
            ItemStack bottomSlot1 = bottomChest.getStack(1);
            ItemStack bottomSlot2 = bottomChest.getStack(2);
            ItemStack bottomSlot3 = bottomChest.getStack(3);
            ItemStack bottomSlot4 = bottomChest.getStack(4);
            ItemStack bottomSlot5 = bottomChest.getStack(5);

            if (!bottomSlot0.isOf(Items.OAK_LOG) || bottomSlot0.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 oak log in slot 0 of the bottom chest, but found " + bottomSlot0), bottomChestPos);
            }
            if (!bottomSlot1.isOf(Items.OAK_WOOD) || bottomSlot1.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 oak wood in slot 1 of the bottom chest, but found " + bottomSlot1), bottomChestPos);
            }
            if (!bottomSlot2.isOf(Items.STRIPPED_OAK_LOG) || bottomSlot2.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 stripped oak log in slot 2 of the bottom chest, but found " + bottomSlot2), bottomChestPos);
            }
            if (!bottomSlot3.isOf(Items.STRIPPED_OAK_WOOD) || bottomSlot3.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 stripped oak wood in slot 3 of the bottom chest, but found " + bottomSlot3), bottomChestPos);
            }
            if (!bottomSlot4.isOf(Items.OAK_PLANKS) || bottomSlot4.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 oak planks in slot 4 of the bottom chest, but found " + bottomSlot4), bottomChestPos);
            }
            if (!bottomSlot5.isOf(Items.OAK_STAIRS) || bottomSlot5.getCount() != 1) {
                context.throwPositionedException(Text.of("Expected 1 oak stairs in slot 5 of the bottom chest, but found " + bottomSlot5), bottomChestPos);
            }

            context.complete();
        });
    }

    private XtremeHopperBlockEntity getHopperAt(TestContext context, BlockPos pos) {
        return context.getBlockEntity(pos, XtremeHopperBlockEntity.class);
    }

    private LockableContainerBlockEntity getChestAt(TestContext context, BlockPos pos) {
        return context.getBlockEntity(pos, LockableContainerBlockEntity.class);
    }
}
