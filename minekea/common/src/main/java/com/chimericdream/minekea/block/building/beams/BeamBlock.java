package com.chimericdream.minekea.block.building.beams;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.tags.CommonItemTags;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BeamBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape CORE_SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    protected static final VoxelShape CONNECTED_NORTH_SHAPE = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 5.0);
    protected static final VoxelShape CONNECTED_SOUTH_SHAPE = Block.box(5.0, 5.0, 11.0, 11.0, 11.0, 16.0);
    protected static final VoxelShape CONNECTED_EAST_SHAPE = Block.box(11.0, 5.0, 5.0, 16.0, 11.0, 11.0);
    protected static final VoxelShape CONNECTED_WEST_SHAPE = Block.box(0.0, 5.0, 5.0, 5.0, 11.0, 11.0);
    protected static final VoxelShape CONNECTED_UP_SHAPE = Block.box(5.0, 11.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape CONNECTED_DOWN_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0);

    public static final BooleanProperty CONNECTED_NORTH;
    public static final BooleanProperty CONNECTED_SOUTH;
    public static final BooleanProperty CONNECTED_EAST;
    public static final BooleanProperty CONNECTED_WEST;
    public static final BooleanProperty CONNECTED_UP;
    public static final BooleanProperty CONNECTED_DOWN;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    static {
        CONNECTED_NORTH = BooleanProperty.create("connected_north");
        CONNECTED_SOUTH = BooleanProperty.create("connected_south");
        CONNECTED_EAST = BooleanProperty.create("connected_east");
        CONNECTED_WEST = BooleanProperty.create("connected_west");
        CONNECTED_UP = BooleanProperty.create("connected_up");
        CONNECTED_DOWN = BooleanProperty.create("connected_down");
    }

    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public BeamBlock(BlockConfig config) {
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
                .setValue(CONNECTED_UP, false)
                .setValue(CONNECTED_DOWN, false)
                .setValue(WATERLOGGED, false)
        );
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "building/beams/" + material);
    }

    public BlockConfig getConfig() {
        return config;
    }

    private boolean shouldConnect(BlockPlaceContext ctx, Direction direction) {
        return shouldConnect(ctx.getLevel(), ctx.getClickedPos(), direction);
    }

    private boolean shouldConnect(BlockGetter world, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = world.getBlockState(neighborPos);

        if (neighborState.is(Blocks.AIR)) {
            return false;
        }

        return neighborState.getFluidState().isEmpty() || neighborState.is(MinekeaBlockTags.BEAMS);
    }

    private double getPartialCoord(Direction hitSide, double coord) {
        double offset = 0.00001;

        if (hitSide == Direction.EAST || hitSide == Direction.SOUTH || hitSide == Direction.UP) {
            offset = -1 * offset;
        }

        int floor = Mth.floor(coord + offset);

        return coord - (double) floor;
    }

    // @TODO: Add "override" versions of the CONNECTED_* properties to allow for more granular control of the beam connections
    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(CommonItemTags.WRENCHES)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Direction hitSide = hit.getDirection();
        Vec3 hitPos = hit.getLocation();
        BooleanProperty connection = getConnectionProperty(hitSide);

        double x = getPartialCoord(hitSide, hitPos.x);
        double y = getPartialCoord(hitSide, hitPos.y);
        double z = getPartialCoord(hitSide, hitPos.z);

        double UPPER_ARM_START = 0.687500;
        double LOWER_ARM_END = 0.312500;

        if (x > UPPER_ARM_START) {
            connection = CONNECTED_EAST;
        } else if (y > UPPER_ARM_START) {
            connection = CONNECTED_UP;
        } else if (z > UPPER_ARM_START) {
            connection = CONNECTED_SOUTH;
        } else if (x < LOWER_ARM_END) {
            connection = CONNECTED_WEST;
        } else if (y < LOWER_ARM_END) {
            connection = CONNECTED_DOWN;
        } else if (z < LOWER_ARM_END) {
            connection = CONNECTED_NORTH;
        }

        world.setBlockAndUpdate(pos, state.setValue(connection, !state.getValue(connection)));
        world.blockEntityChanged(pos);

        if (!world.isClientSide()) {
            world.playSound(null, pos, SoundEvents.SPYGLASS_USE, SoundSource.AMBIENT, 2.0F, 1.5F);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER)
            .setValue(CONNECTED_NORTH, shouldConnect(ctx, Direction.NORTH))
            .setValue(CONNECTED_SOUTH, shouldConnect(ctx, Direction.SOUTH))
            .setValue(CONNECTED_EAST, shouldConnect(ctx, Direction.EAST))
            .setValue(CONNECTED_WEST, shouldConnect(ctx, Direction.WEST))
            .setValue(CONNECTED_UP, shouldConnect(ctx, Direction.UP))
            .setValue(CONNECTED_DOWN, shouldConnect(ctx, Direction.DOWN))
            .setValue(getConnectionProperty(ctx.getClickedFace()), true);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if (neighborState.is(Blocks.AIR)) {
            return state.setValue(getConnectionProperty(direction), false);
        }

        return state.setValue(
            getConnectionProperty(direction),
            neighborState.getFluidState().isEmpty() || neighborState.is(MinekeaBlockTags.BEAMS)
        );
    }

    private BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> CONNECTED_NORTH;
            case SOUTH -> CONNECTED_SOUTH;
            case EAST -> CONNECTED_EAST;
            case WEST -> CONNECTED_WEST;
            case UP -> CONNECTED_UP;
            case DOWN -> CONNECTED_DOWN;
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            CONNECTED_NORTH,
            CONNECTED_SOUTH,
            CONNECTED_EAST,
            CONNECTED_WEST,
            CONNECTED_UP,
            CONNECTED_DOWN,
            WATERLOGGED
        );
    }

    public PushReaction getPistonBehavior(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        boolean connectedNorth = state.getValue(CONNECTED_NORTH);
        boolean connectedSouth = state.getValue(CONNECTED_SOUTH);
        boolean connectedEast = state.getValue(CONNECTED_EAST);
        boolean connectedWest = state.getValue(CONNECTED_WEST);
        boolean connectedUp = state.getValue(CONNECTED_UP);
        boolean connectedDown = state.getValue(CONNECTED_DOWN);

        VoxelShape outline = CORE_SHAPE;

        if (connectedNorth) {
            outline = Shapes.or(outline, CONNECTED_NORTH_SHAPE);
        }
        if (connectedSouth) {
            outline = Shapes.or(outline, CONNECTED_SOUTH_SHAPE);
        }
        if (connectedEast) {
            outline = Shapes.or(outline, CONNECTED_EAST_SHAPE);
        }
        if (connectedWest) {
            outline = Shapes.or(outline, CONNECTED_WEST_SHAPE);
        }
        if (connectedUp) {
            outline = Shapes.or(outline, CONNECTED_UP_SHAPE);
        }
        if (connectedDown) {
            outline = Shapes.or(outline, CONNECTED_DOWN_SHAPE);
        }

        return outline;
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

//    public void register() {
//        RegistryHelpers.registerBlockWithItem(this, BLOCK_ID);
//        FabricItemGroupEventHelpers.addBlockToItemGroup(this, MinekeaItemGroups.BEAM_ITEM_GROUP_KEY);
//
//        if (config.isFlammable()) {
//            FabricRegistryHelpers.registerFlammableBlock(this);
//        }
//    }
//
}
