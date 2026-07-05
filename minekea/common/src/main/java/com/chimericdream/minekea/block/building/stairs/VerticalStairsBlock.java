package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class VerticalStairsBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape NORTH_SHAPE = Shapes.or(
        Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0),
        Block.box(8.0, 0.0, 8.0, 16.0, 16.0, 16.0)
    );
    public static final VoxelShape EAST_SHAPE = Shapes.or(
        Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0),
        Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 8.0)
    );
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(
        Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0),
        Block.box(8.0, 0.0, 8.0, 16.0, 16.0, 16.0)
    );
    public static final VoxelShape WEST_SHAPE = Shapes.or(
        Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0),
        Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 8.0)
    );

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }

    public ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public VerticalStairsBlock(BlockConfig config) {
        this(config, makeId(config.getMaterial()));
    }

    public VerticalStairsBlock(BlockConfig config, ResourceLocation id) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(id)));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = id;
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/vertical/%s", material));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(FACING, ctx.getPlayer() == null ? Direction.NORTH : ctx.getPlayer().getDirection().getOpposite())
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        return switch (facing) {
            case EAST -> VerticalStairsBlock.EAST_SHAPE;
            case SOUTH -> VerticalStairsBlock.SOUTH_SHAPE;
            case WEST -> VerticalStairsBlock.WEST_SHAPE;
            default -> VerticalStairsBlock.NORTH_SHAPE;
        };
    }
}
