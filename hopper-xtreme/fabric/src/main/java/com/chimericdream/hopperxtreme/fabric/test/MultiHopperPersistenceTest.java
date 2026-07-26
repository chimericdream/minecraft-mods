package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.AbstractMultiHopperBlock;
import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A multi-hopper's round-robin cursor ({@code lastDirection}) must survive an unload/reload. Before
 * it was persisted to NBT it reset to the vertical direction on load, so a hopper that had just
 * handed off to NORTH would repeat NORTH after reloading instead of advancing to the next connected
 * side.
 */
@SuppressWarnings("unused")
public class MultiHopperPersistenceTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @GameTest
    public void lastDirectionSurvivesReload(GameTestHelper context) {
        // Two connected sides so the cursor has somewhere to advance to; the vertical (DOWN) side is
        // left unconnected, so a fresh cursor starts by handing off to NORTH.
        BlockState state = ModBlocks.MULTI_HOPPER.get().defaultBlockState()
            .setValue(AbstractMultiHopperBlock.NORTH_CONNECTED, true)
            .setValue(AbstractMultiHopperBlock.SOUTH_CONNECTED, true);
        context.setBlock(POS, state);

        XtremeMultiHopperBlockEntity be = context.getBlockEntity(POS, XtremeMultiHopperBlockEntity.class);

        Direction first = be.getNextDirection();
        context.assertTrue(first == Direction.NORTH, "expected the first hand-off to be NORTH, got " + first);

        HolderLookup.Provider provider = context.getLevel().registryAccess();
        CompoundTag tag = be.saveWithFullMetadata(provider);
        BlockEntity reloaded = BlockEntity.loadStatic(context.absolutePos(POS), state, tag, provider);

        context.assertTrue(reloaded instanceof XtremeMultiHopperBlockEntity, "the reloaded block entity should keep its type");
        XtremeMultiHopperBlockEntity restored = (XtremeMultiHopperBlockEntity) reloaded;

        // If lastDirection persisted (= NORTH) the next hand-off advances to SOUTH; if it had reset to
        // the vertical default it would wrongly repeat NORTH.
        Direction next = restored.getNextDirection();
        context.assertTrue(next == Direction.SOUTH, "expected the reloaded hopper to resume at SOUTH, got " + next);

        context.succeed();
    }
}
