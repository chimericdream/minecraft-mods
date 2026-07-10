package com.chimericdream.minekea.block.furniture.tables;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.text.TextHelpers;
import com.chimericdream.minekea.ModInfo;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class TableBlock extends Block implements SimpleWaterloggedBlock {
    public static final String TOOLTIP_KEY = "block.minekea.furniture.tables.tooltip";

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static final BooleanProperty NORTH_CONNECTED;
    public static final BooleanProperty SOUTH_CONNECTED;
    public static final BooleanProperty EAST_CONNECTED;
    public static final BooleanProperty WEST_CONNECTED;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape TABLE_SURFACE_SHAPE;
    private static final VoxelShape[] LEG_SHAPES;

    static {
        TABLE_SURFACE_SHAPE = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        LEG_SHAPES = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 2.0, 14.0, 2.0), // north-west
            Block.box(14.0, 0.0, 0.0, 16.0, 14.0, 2.0), // north-east
            Block.box(14.0, 0.0, 14.0, 16.0, 14.0, 16.0), // south-east
            Block.box(0.0, 0.0, 14.0, 2.0, 14.0, 16.0) // south-west
        };

        NORTH_CONNECTED = BooleanProperty.create("north_connected");
        SOUTH_CONNECTED = BooleanProperty.create("south_connected");
        EAST_CONNECTED = BooleanProperty.create("east_connected");
        WEST_CONNECTED = BooleanProperty.create("west_connected");
    }

    public TableBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(NORTH_CONNECTED, false)
                .setValue(SOUTH_CONNECTED, false)
                .setValue(EAST_CONNECTED, false)
                .setValue(WEST_CONNECTED, false)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/tables/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    public List<Component> getTooltip() {
        return List.of(TextHelpers.getTooltip(TOOLTIP_KEY));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH_CONNECTED, SOUTH_CONNECTED, EAST_CONNECTED, WEST_CONNECTED, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
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

        return direction.getAxis().isHorizontal()
            ? this.getUpdatedState(state, neighborState, direction)
            : super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private BlockState getUpdatedState(BlockState state, BlockState neighborState, Direction direction) {
        if (direction == Direction.NORTH) {
            if (neighborState.is(this)) {
                return state.setValue(NORTH_CONNECTED, true);
            } else {
                return state.setValue(NORTH_CONNECTED, false);
            }
        }

        if (direction == Direction.SOUTH) {
            if (neighborState.is(this)) {
                return state.setValue(SOUTH_CONNECTED, true);
            } else {
                return state.setValue(SOUTH_CONNECTED, false);
            }
        }

        if (direction == Direction.EAST) {
            if (neighborState.is(this)) {
                return state.setValue(EAST_CONNECTED, true);
            } else {
                return state.setValue(EAST_CONNECTED, false);
            }
        }

        if (direction == Direction.WEST) {
            if (neighborState.is(this)) {
                return state.setValue(WEST_CONNECTED, true);
            } else {
                return state.setValue(WEST_CONNECTED, false);
            }
        }

        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        boolean north = state.getValue(NORTH_CONNECTED);
        boolean south = state.getValue(SOUTH_CONNECTED);
        boolean east = state.getValue(EAST_CONNECTED);
        boolean west = state.getValue(WEST_CONNECTED);

        if (!north && !south && !east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES);
        }

        if (north && !south && !east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[2], LEG_SHAPES[3]);
        }

        if (!north && south && !east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[0], LEG_SHAPES[1]);
        }

        if (!north && !south && east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[0], LEG_SHAPES[3]);
        }

        if (!north && !south && !east && west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[1], LEG_SHAPES[2]);
        }

        if (north && !south && east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[3]);
        }

        if (north && !south && !east && west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[2]);
        }

        if (!north && south && east && !west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[0]);
        }

        if (!north && south && !east && west) {
            return Shapes.or(TABLE_SURFACE_SHAPE, LEG_SHAPES[1]);
        }

        return Shapes.or(TABLE_SURFACE_SHAPE);
    }
}
