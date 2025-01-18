package com.chimericdream.hopperxtreme.test;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.lib.gametest.TestPos;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class PreventFilterExtractionTest {
    @GameTest(templateName = "hopperxtreme:filter/vanilla_hopper")
    public void vanillaHopperItemTransfer(TestContext context) {
        runTest(context, 8);
    }

    @GameTest(templateName = "hopperxtreme:filter/golden_hopper")
    public void goldenHopperItemTransfer(TestContext context) {
        runTest(context, 4);
    }

    @GameTest(templateName = "hopperxtreme:filter/diamond_hopper")
    public void diamondHopperItemTransfer(TestContext context) {
        runTest(context, 2);
    }

    @GameTest(templateName = "hopperxtreme:filter/netherite_hopper")
    public void netheriteHopperItemTransfer(TestContext context) {
        runTest(context, 1);
    }

    private void runTest(TestContext context, int singleItemTickTime) {
        BlockPos topRedstoneBlockPos = TestPos.of(2, 1, 1);
        BlockPos bottomRedstoneBlockPos = TestPos.of(2, 0, 1);
        BlockPos bottomHopperPos = TestPos.of(1, 0, 1);

        context.removeBlock(topRedstoneBlockPos);
        context.removeBlock(bottomRedstoneBlockPos);

        context.waitAndRun((long) 2 * singleItemTickTime, () -> {
            context.setBlockState(topRedstoneBlockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
            context.setBlockState(bottomRedstoneBlockPos, Blocks.REDSTONE_BLOCK.getDefaultState());

            LootableContainerBlockEntity bottomHopper = getContainerAt(context, bottomHopperPos);
            ItemStack hopperSlot0 = bottomHopper.getStack(0);
            if (!hopperSlot0.isEmpty()) {
                context.throwPositionedException("Expected the bottom hopper to be empty, but found " + hopperSlot0, bottomHopperPos);
            }

            context.complete();
        });
    }

    private LootableContainerBlockEntity getContainerAt(TestContext context, BlockPos pos) {
        BlockEntity container = context.getBlockEntity(pos);
        if (!(container instanceof LootableContainerBlockEntity)) {
            context.throwPositionedException("Expected a container at " + pos, pos);
        }

        assert container instanceof LootableContainerBlockEntity;

        return (LootableContainerBlockEntity) container;
    }
}
