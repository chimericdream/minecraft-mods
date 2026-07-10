package com.chimericdream.minekea.block.building.framed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class FramedPlanksBlock extends Block {
    public static final BooleanProperty CONNECTED_NORTH;
    public static final BooleanProperty CONNECTED_SOUTH;
    public static final BooleanProperty CONNECTED_EAST;
    public static final BooleanProperty CONNECTED_WEST;

    static {
        CONNECTED_NORTH = BooleanProperty.create("connected_north");
        CONNECTED_SOUTH = BooleanProperty.create("connected_south");
        CONNECTED_EAST = BooleanProperty.create("connected_east");
        CONNECTED_WEST = BooleanProperty.create("connected_west");
    }

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public FramedPlanksBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(CONNECTED_NORTH, false)
                .setValue(CONNECTED_SOUTH, false)
                .setValue(CONNECTED_EAST, false)
                .setValue(CONNECTED_WEST, false)
        );
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/general/framed_planks/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            CONNECTED_NORTH,
            CONNECTED_SOUTH,
            CONNECTED_EAST,
            CONNECTED_WEST
        );
    }

    protected boolean shouldConnectNorth(BlockGetter world, BlockPos pos) {
        return shouldConnectNorth(world, pos, null);
    }

    protected boolean shouldConnectNorth(BlockGetter world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.relative(Direction.NORTH);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.is(this)) {
            return false;
        }

        if (neighbor.is(this) && (neighbor.getValue(CONNECTED_EAST) || neighbor.getValue(CONNECTED_WEST))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.getValue(CONNECTED_EAST) || self.getValue(CONNECTED_WEST));
    }

    protected boolean shouldConnectSouth(BlockGetter world, BlockPos pos) {
        return shouldConnectSouth(world, pos, null);
    }

    protected boolean shouldConnectSouth(BlockGetter world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.relative(Direction.SOUTH);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.is(this)) {
            return false;
        }

        if (neighbor.is(this) && (neighbor.getValue(CONNECTED_EAST) || neighbor.getValue(CONNECTED_WEST))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.getValue(CONNECTED_EAST) || self.getValue(CONNECTED_WEST));
    }

    protected boolean shouldConnectEast(BlockGetter world, BlockPos pos) {
        return shouldConnectEast(world, pos, null);
    }

    protected boolean shouldConnectEast(BlockGetter world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.relative(Direction.EAST);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.is(this)) {
            return false;
        }

        if (neighbor.is(this) && (neighbor.getValue(CONNECTED_NORTH) || neighbor.getValue(CONNECTED_SOUTH))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.getValue(CONNECTED_NORTH) || self.getValue(CONNECTED_SOUTH));
    }

    protected boolean shouldConnectWest(BlockGetter world, BlockPos pos) {
        return shouldConnectWest(world, pos, null);
    }

    protected boolean shouldConnectWest(BlockGetter world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.relative(Direction.WEST);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.is(this)) {
            return false;
        }

        if (neighbor.is(this) && (neighbor.getValue(CONNECTED_NORTH) || neighbor.getValue(CONNECTED_SOUTH))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.getValue(CONNECTED_NORTH) || self.getValue(CONNECTED_SOUTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (ctx.getPlayer() == null) {
            return this.defaultBlockState();
        }

        boolean connectNorth = shouldConnectNorth(ctx.getLevel(), ctx.getClickedPos());
        boolean connectSouth = shouldConnectSouth(ctx.getLevel(), ctx.getClickedPos());
        boolean connectEast = shouldConnectEast(ctx.getLevel(), ctx.getClickedPos());
        boolean connectWest = shouldConnectWest(ctx.getLevel(), ctx.getClickedPos());

        boolean isNorthSouth = connectNorth || connectSouth;

        return this.defaultBlockState()
            .setValue(CONNECTED_NORTH, connectNorth)
            .setValue(CONNECTED_SOUTH, connectSouth)
            .setValue(CONNECTED_EAST, !isNorthSouth && connectEast)
            .setValue(CONNECTED_WEST, !isNorthSouth && connectWest);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction.getAxis().isVertical()) {
            return state;
        }

        boolean connectNorth = shouldConnectNorth(world, pos, state);
        boolean connectSouth = shouldConnectSouth(world, pos, state);
        boolean connectEast = shouldConnectEast(world, pos, state);
        boolean connectWest = shouldConnectWest(world, pos, state);

        boolean isNorthSouth = connectNorth || connectSouth;

        return state
            .setValue(CONNECTED_NORTH, connectNorth)
            .setValue(CONNECTED_SOUTH, connectSouth)
            .setValue(CONNECTED_EAST, !isNorthSouth && connectEast)
            .setValue(CONNECTED_WEST, !isNorthSouth && connectWest);
    }
}
