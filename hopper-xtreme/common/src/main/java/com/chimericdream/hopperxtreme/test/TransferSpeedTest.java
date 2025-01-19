package com.chimericdream.hopperxtreme.test;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class TransferSpeedTest {
    @GameTest(templateName = "hopperxtreme:transfer_speed/honeyed_hopper")
    public void honeyedHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 80);
    }

    @GameTest(templateName = "hopperxtreme:transfer_speed/copper_hopper")
    public void copperHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 32, false);
    }

    @GameTest(templateName = "hopperxtreme:transfer_speed/golden_hopper")
    public void goldenHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 16);
    }

    @GameTest(templateName = "hopperxtreme:transfer_speed/diamond_hopper")
    public void diamondHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 8);
    }

    @GameTest(templateName = "hopperxtreme:transfer_speed/netherite_hopper")
    public void netheriteHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 4);
    }

    private void runItemTransferSpeedTest(TestContext context, int fourItemsTickTime) {
        runItemTransferSpeedTest(context, fourItemsTickTime, true);
    }

    private void runItemTransferSpeedTest(TestContext context, int fourItemsTickTime, boolean breakRedstoneBlock) {
        BlockPos redstoneBlockPos = new BlockPos(2, 1, 1);
        BlockPos topChestPos = new BlockPos(1, 2, 1);
        BlockPos hopperPos = new BlockPos(1, 1, 1);
        BlockPos bottomChestPos = new BlockPos(1, 0, 1);

        LockableContainerBlockEntity topChest = getChestAt(context, topChestPos);
        topChest.setStack(0, new ItemStack(Items.REDSTONE_BLOCK, 64));

        if (breakRedstoneBlock) {
            context.removeBlock(redstoneBlockPos);
        }

        context.waitAndRun(fourItemsTickTime, () -> {
            if (breakRedstoneBlock) {
                context.setBlockState(redstoneBlockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
            }

            XtremeHopperBlockEntity hopper = getHopperAt(context, hopperPos);

            ItemStack hopperSlot0 = hopper.getStack(0);
            if (!hopperSlot0.isOf(Items.REDSTONE_BLOCK) || hopperSlot0.getCount() != 1) {
                context.throwPositionedException("Expected 1 redstone block in the hopper, but found " + hopperSlot0, hopperPos);
            }

            LockableContainerBlockEntity bottomChest = getChestAt(context, bottomChestPos);

            ItemStack bottomSlot0 = bottomChest.getStack(0);
            if (!bottomSlot0.isOf(Items.REDSTONE_BLOCK) || bottomSlot0.getCount() != 3) {
                context.throwPositionedException("Expected 3 redstone blocks in the bottom chest, but found " + bottomSlot0, bottomChestPos);
            }

            context.complete();
        });
    }

    private XtremeHopperBlockEntity getHopperAt(TestContext context, BlockPos pos) {
        BlockEntity hopper = context.getBlockEntity(pos);
        if (!(hopper instanceof XtremeHopperBlockEntity)) {
            context.throwPositionedException("Expected a hopper at " + pos, pos);
        }

        assert hopper instanceof XtremeHopperBlockEntity;

        return (XtremeHopperBlockEntity) hopper;
    }

    private LockableContainerBlockEntity getChestAt(TestContext context, BlockPos pos) {
        BlockEntity chest = context.getBlockEntity(pos);
        if (!(chest instanceof LockableContainerBlockEntity)) {
            context.throwPositionedException("Expected a container at " + pos, pos);
        }

        assert chest instanceof LockableContainerBlockEntity;

        return (LockableContainerBlockEntity) chest;
    }
}
