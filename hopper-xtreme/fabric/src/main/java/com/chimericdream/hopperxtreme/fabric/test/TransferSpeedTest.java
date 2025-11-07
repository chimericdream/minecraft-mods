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
public class TransferSpeedTest {
    @GameTest(structure = "hopperxtreme:transfer_speed/honeyed_hopper", maxTicks = 80)
    public void honeyedHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 80);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/copper_hopper", maxTicks = 32)
    public void copperHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 32, false);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/golden_hopper")
    public void goldenHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 16);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/diamond_hopper")
    public void diamondHopperItemTransfer(TestContext context) {
        runItemTransferSpeedTest(context, 8);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/netherite_hopper")
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
                context.throwPositionedException(Text.of("Expected 1 redstone block in the hopper, but found " + hopperSlot0), hopperPos);
            }

            LockableContainerBlockEntity bottomChest = getChestAt(context, bottomChestPos);

            ItemStack bottomSlot0 = bottomChest.getStack(0);
            if (!bottomSlot0.isOf(Items.REDSTONE_BLOCK) || bottomSlot0.getCount() != 3) {
                context.throwPositionedException(Text.of("Expected 3 redstone blocks in the bottom chest, but found " + bottomSlot0), bottomChestPos);
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
