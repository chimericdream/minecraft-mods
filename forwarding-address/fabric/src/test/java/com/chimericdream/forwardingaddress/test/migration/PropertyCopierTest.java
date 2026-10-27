package com.chimericdream.forwardingaddress.test.migration;

import com.chimericdream.forwardingaddress.migration.PropertyCopier;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyCopierTest extends BootstrapMinecraft {
    @Test
    void copiesEveryPropertyBetweenBlocksOfTheSameShape() {
        BlockState from = Blocks.OAK_STAIRS.defaultBlockState()
            .setValue(StairBlock.FACING, Direction.WEST)
            .setValue(StairBlock.HALF, Half.TOP)
            .setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT)
            .setValue(StairBlock.WATERLOGGED, true);

        BlockState result = PropertyCopier.copy(from, Blocks.BIRCH_STAIRS.defaultBlockState());

        assertSame(Blocks.BIRCH_STAIRS, result.getBlock());
        assertEquals(Direction.WEST, result.getValue(StairBlock.FACING));
        assertEquals(Half.TOP, result.getValue(StairBlock.HALF));
        assertEquals(StairsShape.INNER_LEFT, result.getValue(StairBlock.SHAPE));
        assertTrue(result.getValue(StairBlock.WATERLOGGED));
    }

    @Test
    void copiesOnlyWhatTheTargetShares() {
        BlockState from = Blocks.OAK_STAIRS.defaultBlockState()
            .setValue(StairBlock.FACING, Direction.WEST)
            .setValue(StairBlock.WATERLOGGED, true);

        BlockState result = PropertyCopier.copy(from, Blocks.OAK_SLAB.defaultBlockState());

        assertTrue(result.getValue(SlabBlock.WATERLOGGED));
        assertEquals(SlabType.BOTTOM, result.getValue(SlabBlock.TYPE));
    }

    @Test
    void keepsTheTargetDefaultWhenTheValueIsNotAllowed() {
        BlockState from = Blocks.BARREL.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);

        // A dispenser also uses "facing" with every direction, so the value copies...
        BlockState dispenser = PropertyCopier.copy(from, Blocks.DISPENSER.defaultBlockState());
        assertEquals(Direction.UP, dispenser.getValue(BlockStateProperties.FACING));

        // ...but a furnace only allows horizontal ones, so it keeps its default.
        BlockState furnace = PropertyCopier.copy(from, Blocks.FURNACE.defaultBlockState());
        assertEquals(Direction.NORTH, furnace.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
