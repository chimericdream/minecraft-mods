package com.chimericdream.hopperxtreme.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class PreventFilterExtractionTest {
    @GameTest(structure = "hopperxtreme:filter/vanilla_hopper")
    public void vanillaHopperItemTransfer(TestContext context) {
        runTest(context, 8);
    }

    @GameTest(structure = "hopperxtreme:filter/golden_hopper")
    public void goldenHopperItemTransfer(TestContext context) {
        runTest(context, 4);
    }

    @GameTest(structure = "hopperxtreme:filter/diamond_hopper")
    public void diamondHopperItemTransfer(TestContext context) {
        runTest(context, 2);
    }

    @GameTest(structure = "hopperxtreme:filter/netherite_hopper")
    public void netheriteHopperItemTransfer(TestContext context) {
        runTest(context, 1);
    }

    private void runTest(TestContext context, int singleItemTickTime) {
        BlockPos bottomHopperPos = new BlockPos(1, 0, 1);

        context.waitAndRun((long) 2 * singleItemTickTime, () -> {
            LootableContainerBlockEntity bottomHopper = getContainerAt(context, bottomHopperPos);
            ItemStack hopperSlot0 = bottomHopper.getStack(0);
            if (!hopperSlot0.isEmpty()) {
                context.throwPositionedException(Text.of("Expected the bottom hopper to be empty, but found " + hopperSlot0), bottomHopperPos);
            }

            context.complete();
        });
    }

    private LootableContainerBlockEntity getContainerAt(TestContext context, BlockPos pos) {
        return context.getBlockEntity(pos, LootableContainerBlockEntity.class);
    }
}
