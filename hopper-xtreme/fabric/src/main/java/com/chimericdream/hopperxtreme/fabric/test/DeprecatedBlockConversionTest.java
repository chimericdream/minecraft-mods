package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class DeprecatedBlockConversionTest {
    private static final int FILTER_SLOT = 5;

    /**
     * A deprecated {@code filtered_*} block placed in the world should convert to its canonical
     * (filter-capable) equivalent on its first server tick, preserving its inventory and installed
     * filter because both blocks share the same block entity type (so the whole block entity, with
     * every field, survives the swap).
     */
    @GameTest(structure = "hopperxtreme:deprecation/conversion")
    public void deprecatedHopperConvertsOnTick(GameTestHelper context) {
        BlockPos hopperPos = new BlockPos(1, 1, 1);

        context.setBlock(hopperPos, ModBlocks.FILTERED_GOLDEN_HOPPER.get().defaultBlockState());

        XtremeHopperBlockEntity hopper = context.getBlockEntity(hopperPos, XtremeHopperBlockEntity.class);
        hopper.setItem(0, new ItemStack(Items.DIAMOND, 5));
        hopper.setItem(FILTER_SLOT, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));

        context.runAfterDelay(2, () -> {
            BlockState state = context.getBlockState(hopperPos);
            XtremeHopperBlockEntity converted = context.getBlockEntity(hopperPos, XtremeHopperBlockEntity.class);

            ItemStack storage = converted.getItem(0);
            ItemStack filter = converted.getItem(FILTER_SLOT);

            boolean converted2Golden = state.is(ModBlocks.GOLDEN_HOPPER.get());
            boolean storageOk = storage.is(Items.DIAMOND) && storage.getCount() == 5;
            boolean filterOk = filter.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get());

            if (!converted2Golden || !converted.withFilter || !storageOk || !filterOk) {
                context.fail(Component.literal(
                    "block=" + state.getBlock() + " withFilter=" + converted.withFilter
                        + " slot0=" + storage + " slot5=" + filter
                ), hopperPos);
                return;
            }

            // The contents must be preserved in place, not dropped (which would duplicate them).
            context.assertItemEntityCountIs(Items.DIAMOND, hopperPos, 4.0, 0);

            context.succeed();
        });
    }
}
