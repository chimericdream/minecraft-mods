package com.chimericdream.jdcrafte.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A single-block feeding trough with a "level" property (0-3) representing how full it is. The
 * trough is visually symmetric front-to-back, so it only needs to track which horizontal axis it
 * runs along rather than a full facing direction.
 */
public class FeedingTroughBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);

    // Shape is authored against the trough model as it sits in models/block/trough.json (long axis
    // running along X) and rotated 90 degrees for the Z-axis orientation. The four angled legs are
    // collapsed into two bounding boxes (one per end) rather than modeled precisely.
    private static final Map<Direction.Axis, VoxelShape> SHAPES;

    static {
        VoxelShape xAxis = Shapes.or(
            Block.box(0, 3, 3, 16, 4, 13),
            Block.box(0, 4, 3, 16, 10, 4),
            Block.box(0, 4, 12, 16, 10, 13),
            Block.box(0, 4, 4, 1, 10, 12),
            Block.box(15, 4, 4, 16, 10, 12),
            Block.box(0.25, 0, 4, 1.25, 4, 12),
            Block.box(14.75, 0, 4, 15.75, 4, 12)
        );

        SHAPES = Map.of(
            Direction.Axis.X, xAxis,
            Direction.Axis.Z, Shapes.rotate(xAxis, OctahedralGroup.BLOCK_ROT_Y_90)
        );
    }

    public FeedingTroughBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(LEVEL, 0)
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(FeedingTroughBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, LEVEL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }
}
