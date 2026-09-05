package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.entity.AbstractXtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/**
 * Regression test: breaking a filtered hopper/multi-hopper must drop the installed filter item along
 * with the rest of the inventory. {@code AbstractXtremeHopperBlockEntity#getContainerSize()}
 * deliberately excludes the filter slot (it sits one index past the storage slots) so
 * insertion/extraction/fullness logic and the menu never see it — but vanilla's default block-break
 * drop path ({@code BlockEntity#preRemoveSideEffects}) walks exactly {@code getContainerSize()} slots,
 * so with no override the filter item was silently deleted instead of dropping.
 */
@SuppressWarnings("unused")
public class FilterDropOnBreakTest {
    private static final int FILTER_SLOT = 5;
    private static final BlockPos HOPPER_POS = new BlockPos(1, 1, 1);

    @GameTest
    public void breakingFilteredHopperDropsTheFilter(GameTestHelper context) {
        runTest(context, ModBlocks.FILTERED_GOLDEN_HOPPER.get());
    }

    @GameTest
    public void breakingFilteredMultiHopperDropsTheFilter(GameTestHelper context) {
        runTest(context, ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get());
    }

    private void runTest(GameTestHelper context, Block filteredHopperBlock) {
        context.setBlock(HOPPER_POS, filteredHopperBlock.defaultBlockState());

        AbstractXtremeHopperBlockEntity hopper = context.getBlockEntity(HOPPER_POS, AbstractXtremeHopperBlockEntity.class);
        hopper.setItem(0, new ItemStack(Items.DIAMOND, 5));
        hopper.setItem(FILTER_SLOT, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));

        context.destroyBlock(HOPPER_POS);

        context.assertItemEntityCountIs(Items.DIAMOND, HOPPER_POS, 4.0, 5);
        context.assertItemEntityCountIs(ModItems.HOPPER_ITEM_FILTER_ITEM.get(), HOPPER_POS, 4.0, 1);

        context.succeed();
    }
}
