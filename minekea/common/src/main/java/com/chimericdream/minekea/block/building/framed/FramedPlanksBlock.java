package com.chimericdream.minekea.block.building.framed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class FramedPlanksBlock extends Block {
    public static final BooleanProperty CONNECTED_NORTH;
    public static final BooleanProperty CONNECTED_SOUTH;
    public static final BooleanProperty CONNECTED_EAST;
    public static final BooleanProperty CONNECTED_WEST;

    static {
        CONNECTED_NORTH = BooleanProperty.of("connected_north");
        CONNECTED_SOUTH = BooleanProperty.of("connected_south");
        CONNECTED_EAST = BooleanProperty.of("connected_east");
        CONNECTED_WEST = BooleanProperty.of("connected_west");
    }

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public FramedPlanksBlock(BlockConfig config) {
        super(config.getBaseSettings().registryKey(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(CONNECTED_NORTH, false)
                .with(CONNECTED_SOUTH, false)
                .with(CONNECTED_EAST, false)
                .with(CONNECTED_WEST, false)
        );
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("building/general/framed_planks/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
            CONNECTED_NORTH,
            CONNECTED_SOUTH,
            CONNECTED_EAST,
            CONNECTED_WEST
        );
    }

    protected boolean shouldConnectNorth(BlockView world, BlockPos pos) {
        return shouldConnectNorth(world, pos, null);
    }

    protected boolean shouldConnectNorth(BlockView world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.offset(Direction.NORTH);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.isOf(this)) {
            return false;
        }

        if (neighbor.isOf(this) && (neighbor.get(CONNECTED_EAST) || neighbor.get(CONNECTED_WEST))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.get(CONNECTED_EAST) || self.get(CONNECTED_WEST));
    }

    protected boolean shouldConnectSouth(BlockView world, BlockPos pos) {
        return shouldConnectSouth(world, pos, null);
    }

    protected boolean shouldConnectSouth(BlockView world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.offset(Direction.SOUTH);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.isOf(this)) {
            return false;
        }

        if (neighbor.isOf(this) && (neighbor.get(CONNECTED_EAST) || neighbor.get(CONNECTED_WEST))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.get(CONNECTED_EAST) || self.get(CONNECTED_WEST));
    }

    protected boolean shouldConnectEast(BlockView world, BlockPos pos) {
        return shouldConnectEast(world, pos, null);
    }

    protected boolean shouldConnectEast(BlockView world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.offset(Direction.EAST);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.isOf(this)) {
            return false;
        }

        if (neighbor.isOf(this) && (neighbor.get(CONNECTED_NORTH) || neighbor.get(CONNECTED_SOUTH))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.get(CONNECTED_NORTH) || self.get(CONNECTED_SOUTH));
    }

    protected boolean shouldConnectWest(BlockView world, BlockPos pos) {
        return shouldConnectWest(world, pos, null);
    }

    protected boolean shouldConnectWest(BlockView world, BlockPos pos, @Nullable BlockState self) {
        BlockPos neighborPos = pos.offset(Direction.WEST);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.isOf(this)) {
            return false;
        }

        if (neighbor.isOf(this) && (neighbor.get(CONNECTED_NORTH) || neighbor.get(CONNECTED_SOUTH))) {
            return false;
        }

        if (self == null) {
            return true;
        }

        return !(self.get(CONNECTED_NORTH) || self.get(CONNECTED_SOUTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getPlayer() == null) {
            return this.getDefaultState();
        }

        boolean connectNorth = shouldConnectNorth(ctx.getWorld(), ctx.getBlockPos());
        boolean connectSouth = shouldConnectSouth(ctx.getWorld(), ctx.getBlockPos());
        boolean connectEast = shouldConnectEast(ctx.getWorld(), ctx.getBlockPos());
        boolean connectWest = shouldConnectWest(ctx.getWorld(), ctx.getBlockPos());

        boolean isNorthSouth = connectNorth || connectSouth;

        return this.getDefaultState()
            .with(CONNECTED_NORTH, connectNorth)
            .with(CONNECTED_SOUTH, connectSouth)
            .with(CONNECTED_EAST, !isNorthSouth && connectEast)
            .with(CONNECTED_WEST, !isNorthSouth && connectWest);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getAxis().isVertical()) {
            return state;
        }

        boolean connectNorth = shouldConnectNorth(world, pos, state);
        boolean connectSouth = shouldConnectSouth(world, pos, state);
        boolean connectEast = shouldConnectEast(world, pos, state);
        boolean connectWest = shouldConnectWest(world, pos, state);

        boolean isNorthSouth = connectNorth || connectSouth;

        return state
            .with(CONNECTED_NORTH, connectNorth)
            .with(CONNECTED_SOUTH, connectSouth)
            .with(CONNECTED_EAST, !isNorthSouth && connectEast)
            .with(CONNECTED_WEST, !isNorthSouth && connectWest);
    }
}
