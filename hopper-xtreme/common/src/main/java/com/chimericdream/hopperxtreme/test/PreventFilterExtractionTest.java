package com.chimericdream.hopperxtreme.test;

import net.minecraft.block.entity.BlockEntity;
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
        BlockPos bottomHopperPos = new BlockPos(1, 0, 1);

        context.waitAndRun((long) 2 * singleItemTickTime, () -> {
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
