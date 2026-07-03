package com.chimericdream.hopperxtreme.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

@SuppressWarnings("unused")
public class PreventFilterExtractionTest {
    @GameTest(structure = "hopperxtreme:filter/vanilla_hopper")
    public void vanillaHopperItemTransfer(GameTestHelper context) {
        runTest(context, 8);
    }

    @GameTest(structure = "hopperxtreme:filter/golden_hopper")
    public void goldenHopperItemTransfer(GameTestHelper context) {
        runTest(context, 4);
    }

    @GameTest(structure = "hopperxtreme:filter/diamond_hopper")
    public void diamondHopperItemTransfer(GameTestHelper context) {
        runTest(context, 2);
    }

    @GameTest(structure = "hopperxtreme:filter/netherite_hopper")
    public void netheriteHopperItemTransfer(GameTestHelper context) {
        runTest(context, 1);
    }

    private void runTest(GameTestHelper context, int singleItemTickTime) {
        BlockPos bottomHopperPos = new BlockPos(1, 0, 1);

        context.runAfterDelay((long) 2 * singleItemTickTime, () -> {
            BaseContainerBlockEntity bottomHopper = getContainerAt(context, bottomHopperPos);
            ItemStack hopperSlot0 = bottomHopper.getItem(0);
            if (!hopperSlot0.isEmpty()) {
                context.fail(Component.literal("Expected the bottom hopper to be empty, but found " + hopperSlot0), bottomHopperPos);
            }

            context.succeed();
        });
    }

    private BaseContainerBlockEntity getContainerAt(GameTestHelper context, BlockPos pos) {
        return context.getBlockEntity(pos, BaseContainerBlockEntity.class);
    }
}
