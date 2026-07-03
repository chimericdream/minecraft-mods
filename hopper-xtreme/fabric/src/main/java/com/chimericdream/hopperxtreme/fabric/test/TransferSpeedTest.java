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
public class TransferSpeedTest {
    @GameTest(structure = "hopperxtreme:transfer_speed/honeyed_hopper", maxTicks = 80)
    public void honeyedHopperItemTransfer(GameTestHelper context) {
        runItemTransferSpeedTest(context, 80);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/copper_hopper", maxTicks = 32)
    public void copperHopperItemTransfer(GameTestHelper context) {
        runItemTransferSpeedTest(context, 32, false);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/golden_hopper")
    public void goldenHopperItemTransfer(GameTestHelper context) {
        runItemTransferSpeedTest(context, 16);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/diamond_hopper")
    public void diamondHopperItemTransfer(GameTestHelper context) {
        runItemTransferSpeedTest(context, 8);
    }

    @GameTest(structure = "hopperxtreme:transfer_speed/netherite_hopper")
    public void netheriteHopperItemTransfer(GameTestHelper context) {
        runItemTransferSpeedTest(context, 4);
    }

    private void runItemTransferSpeedTest(GameTestHelper context, int fourItemsTickTime) {
        runItemTransferSpeedTest(context, fourItemsTickTime, true);
    }

    private void runItemTransferSpeedTest(GameTestHelper context, int fourItemsTickTime, boolean breakRedstoneBlock) {
        BlockPos redstoneBlockPos = new BlockPos(2, 1, 1);
        BlockPos topChestPos = new BlockPos(1, 2, 1);
        BlockPos hopperPos = new BlockPos(1, 1, 1);
        BlockPos bottomChestPos = new BlockPos(1, 0, 1);

        BaseContainerBlockEntity topChest = getChestAt(context, topChestPos);
        topChest.setItem(0, new ItemStack(Items.REDSTONE_BLOCK, 64));

        if (breakRedstoneBlock) {
            context.destroyBlock(redstoneBlockPos);
        }

        context.runAfterDelay(fourItemsTickTime, () -> {
            if (breakRedstoneBlock) {
                context.setBlock(redstoneBlockPos, Blocks.REDSTONE_BLOCK.defaultBlockState());
            }

            XtremeHopperBlockEntity hopper = getHopperAt(context, hopperPos);

            ItemStack hopperSlot0 = hopper.getItem(0);
            if (!hopperSlot0.is(Items.REDSTONE_BLOCK) || hopperSlot0.getCount() != 1) {
                context.fail(Component.literal("Expected 1 redstone block in the hopper, but found " + hopperSlot0), hopperPos);
            }

            BaseContainerBlockEntity bottomChest = getChestAt(context, bottomChestPos);

            ItemStack bottomSlot0 = bottomChest.getItem(0);
            if (!bottomSlot0.is(Items.REDSTONE_BLOCK) || bottomSlot0.getCount() != 3) {
                context.fail(Component.literal("Expected 3 redstone blocks in the bottom chest, but found " + bottomSlot0), bottomChestPos);
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
