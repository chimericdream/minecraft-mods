package com.chimericdream.hopperxtreme.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractHopperBlock extends BaseEntityBlock {
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

    protected AbstractHopperBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(ENABLED, true));
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
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
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        switch (state.getValue(FACING)) {
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getClickedFace().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).setValue(ENABLED, true);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    static {
        FACING = BlockStateProperties.FACING_HOPPER;
        ENABLED = BlockStateProperties.ENABLED;
        TOP_SHAPE = Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        MIDDLE_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = Shapes.or(MIDDLE_SHAPE, TOP_SHAPE);
        INSIDE_SHAPE = box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
        DEFAULT_SHAPE = Shapes.join(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanOp.ONLY_FIRST);
        DOWN_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
        DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
