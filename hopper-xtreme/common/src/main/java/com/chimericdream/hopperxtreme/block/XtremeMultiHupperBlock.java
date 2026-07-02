package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeMultiHupperBlockEntity;
import com.chimericdream.hopperxtreme.tag.CommonTags;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_MULTI_HUPPER_BLOCK_ENTITY;

public class XtremeMultiHupperBlock extends BaseEntityBlock {
    public static final MapCodec<XtremeMultiHupperBlock> CODEC = simpleCodec(XtremeMultiHupperBlock::create);

    public static final BooleanProperty ENABLED;
    public static final BooleanProperty NORTH_CONNECTED;
    public static final BooleanProperty SOUTH_CONNECTED;
    public static final BooleanProperty EAST_CONNECTED;
    public static final BooleanProperty WEST_CONNECTED;
    public static final BooleanProperty UP_CONNECTED;

    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape OUTSIDE_SHAPE;
    public static final VoxelShape INSIDE_SHAPE;

    private static final VoxelShape DEFAULT_SHAPE;
    private static final VoxelShape UP_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;

    private static final VoxelShape UP_RAYCAST_SHAPE;
    private static final VoxelShape EAST_RAYCAST_SHAPE;
    private static final VoxelShape NORTH_RAYCAST_SHAPE;
    private static final VoxelShape SOUTH_RAYCAST_SHAPE;
    private static final VoxelShape WEST_RAYCAST_SHAPE;

    static {
        ENABLED = BlockStateProperties.ENABLED;
        NORTH_CONNECTED = BooleanProperty.create("north_connected");
        SOUTH_CONNECTED = BooleanProperty.create("south_connected");
        EAST_CONNECTED = BooleanProperty.create("east_connected");
        WEST_CONNECTED = BooleanProperty.create("west_connected");
        UP_CONNECTED = BooleanProperty.create("up_connected");

        BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
        MIDDLE_SHAPE = Block.box(4.0, 6.0, 4.0, 12.0, 12.0, 12.0);
        OUTSIDE_SHAPE = Shapes.or(MIDDLE_SHAPE, BOTTOM_SHAPE);
        INSIDE_SHAPE = box(2.0, 0.0, 2.0, 14.0, 5.0, 14.0);

        DEFAULT_SHAPE = Shapes.join(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanOp.ONLY_FIRST);
        UP_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 12.0, 6.0, 10.0, 16.0, 10.0));
        EAST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(12.0, 8.0, 6.0, 16.0, 12.0, 10.0));
        NORTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 8.0, 0.0, 10.0, 12.0, 4.0));
        SOUTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 8.0, 12.0, 10.0, 12.0, 16.0));
        WEST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(0.0, 8.0, 6.0, 4.0, 12.0, 10.0));

        UP_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(12.0, 6.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 6.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 6.0, 12.0, 10.0, 8.0, 16.0));
        WEST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(0.0, 6.0, 6.0, 4.0, 8.0, 10.0));
    }

    public MapCodec<XtremeMultiHupperBlock> codec() {
        return CODEC;
    }

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static XtremeMultiHupperBlock create(Properties settings) {
        return new XtremeMultiHupperBlock(8, "default");
    }

    public XtremeMultiHupperBlock(int cooldownInTicks, String baseKey) {
        this(cooldownInTicks, baseKey, false);
    }

    public XtremeMultiHupperBlock(int cooldownInTicks, String baseKey, boolean withFilter) {
        super(
            Properties.ofFullCopy(Blocks.HOPPER)
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 4.8F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .setId(REGISTRY_HELPER.makeBlockRegistryKey(baseKey))
        );

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = baseKey;
        this.withFilter = withFilter;

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(NORTH_CONNECTED, false)
                .setValue(SOUTH_CONNECTED, false)
                .setValue(EAST_CONNECTED, false)
                .setValue(WEST_CONNECTED, false)
                .setValue(UP_CONNECTED, false)
                .setValue(ENABLED, true)
        );
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    private BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_CONNECTED;
            case SOUTH -> SOUTH_CONNECTED;
            case EAST -> EAST_CONNECTED;
            case WEST -> WEST_CONNECTED;
            default -> UP_CONNECTED;
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.getValue(NORTH_CONNECTED)) {
            parts.add(NORTH_SHAPE);
        }
        if (state.getValue(SOUTH_CONNECTED)) {
            parts.add(SOUTH_SHAPE);
        }
        if (state.getValue(EAST_CONNECTED)) {
            parts.add(EAST_SHAPE);
        }
        if (state.getValue(WEST_CONNECTED)) {
            parts.add(WEST_SHAPE);
        }
        if (state.getValue(UP_CONNECTED)) {
            parts.add(UP_SHAPE);
        }

        return Shapes.or(DEFAULT_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.getValue(NORTH_CONNECTED)) {
            parts.add(NORTH_RAYCAST_SHAPE);
        }
        if (state.getValue(SOUTH_CONNECTED)) {
            parts.add(SOUTH_RAYCAST_SHAPE);
        }
        if (state.getValue(EAST_CONNECTED)) {
            parts.add(EAST_RAYCAST_SHAPE);
        }
        if (state.getValue(WEST_CONNECTED)) {
            parts.add(WEST_RAYCAST_SHAPE);
        }
        if (state.getValue(UP_CONNECTED)) {
            parts.add(UP_RAYCAST_SHAPE);
        }

        return Shapes.or(INSIDE_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XtremeMultiHupperBlockEntity(pos, state, cooldownInTicks, withFilter);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, XTREME_MULTI_HUPPER_BLOCK_ENTITY.get(), XtremeMultiHupperBlockEntity::serverTick);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof XtremeMultiHupperBlockEntity) {
                player.openMenu((XtremeMultiHupperBlockEntity) blockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

    private double getPartialCoord(Direction hitSide, double coord) {
        double offset = 0.00001;

        if (hitSide == Direction.EAST || hitSide == Direction.SOUTH || hitSide == Direction.UP) {
            offset = -1 * offset;
        }

        int floor = Mth.floor(coord + offset);

        return coord - (double) floor;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Temporary workaround until the next version of Minekea adds its wrench to the common tag.
        if (!stack.is(CommonTags.WRENCHES) && !stack.getItem().builtInRegistryHolder().key().location().equals(ResourceLocation.parse("minekea:tools/wrench"))) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Direction hitSide = hit.getDirection();
        Vec3 hitPos = hit.getLocation();

        double x = getPartialCoord(hitSide, hitPos.x);
        double y = getPartialCoord(hitSide, hitPos.y);
        double z = getPartialCoord(hitSide, hitPos.z);

        double UPPER_ARM_START = 0.75;
        double LOWER_ARM_END = 0.25;

        BooleanProperty connection = getConnectionProperty(hitSide);

        if (x > UPPER_ARM_START) {
            connection = EAST_CONNECTED;
        } else if (z > UPPER_ARM_START) {
            connection = SOUTH_CONNECTED;
        } else if (x < LOWER_ARM_END) {
            connection = WEST_CONNECTED;
        } else if (y > UPPER_ARM_START) {
            connection = UP_CONNECTED;
        } else if (z < LOWER_ARM_END) {
            connection = NORTH_CONNECTED;
        }

        world.setBlockAndUpdate(pos, state.setValue(connection, !state.getValue(connection)));
        world.blockEntityChanged(pos);

        return InteractionResult.CONSUME;
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(Level world, BlockPos pos, BlockState state) {
        boolean bl = !world.hasNeighborSignal(pos);
        if (bl != (Boolean) state.getValue(ENABLED)) {
            world.setBlock(pos, (BlockState) state.setValue(ENABLED, bl), 2);
        }

    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ENABLED, NORTH_CONNECTED, SOUTH_CONNECTED, EAST_CONNECTED, WEST_CONNECTED, UP_CONNECTED});
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof XtremeMultiHupperBlockEntity) {
            XtremeMultiHupperBlockEntity.onEntityCollided(world, pos, state, entity, (XtremeMultiHupperBlockEntity) blockEntity);
        }

    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
