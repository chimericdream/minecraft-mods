package com.chimericdream.hopperxtreme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

abstract public class AbstractHopperBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty ENABLED;

    public static final VoxelShape TOP_SHAPE;
    public static final VoxelShape MIDDLE_SHAPE;
    public static final VoxelShape OUTSIDE_SHAPE;
    public static final VoxelShape INSIDE_SHAPE;

    public static final VoxelShape DEFAULT_SHAPE;
    public static final VoxelShape DOWN_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;
    public static final VoxelShape WEST_SHAPE;

    public static final VoxelShape DOWN_RAYCAST_SHAPE;
    public static final VoxelShape EAST_RAYCAST_SHAPE;
    public static final VoxelShape NORTH_RAYCAST_SHAPE;
    public static final VoxelShape SOUTH_RAYCAST_SHAPE;
    public static final VoxelShape WEST_RAYCAST_SHAPE;

    protected AbstractHopperBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN).with(ENABLED, true));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case DOWN -> {
                return DOWN_SHAPE;
            }
            case NORTH -> {
                return NORTH_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            case WEST -> {
                return WEST_SHAPE;
            }
            case EAST -> {
                return EAST_SHAPE;
            }
            default -> {
                return DEFAULT_SHAPE;
            }
        }
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        switch (state.get(FACING)) {
            case DOWN -> {
                return DOWN_RAYCAST_SHAPE;
            }
            case NORTH -> {
                return NORTH_RAYCAST_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_RAYCAST_SHAPE;
            }
            case WEST -> {
                return WEST_RAYCAST_SHAPE;
            }
            case EAST -> {
                return EAST_RAYCAST_SHAPE;
            }
            default -> {
                return INSIDE_SHAPE;
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide().getOpposite();
        return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).with(ENABLED, true);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    static {
        FACING = Properties.HOPPER_FACING;
        ENABLED = Properties.ENABLED;
        TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
        INSIDE_SHAPE = createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
        DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
        DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
        DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
