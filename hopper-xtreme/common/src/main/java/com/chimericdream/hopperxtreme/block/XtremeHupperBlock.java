package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeHupperBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_HUPPER_BLOCK_ENTITY;

public class XtremeHupperBlock extends BaseEntityBlock {
    public static final MapCodec<XtremeHupperBlock> CODEC = simpleCodec(XtremeHupperBlock::create);

    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty ENABLED;

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
        FACING = EnumProperty.create("facing", Direction.class, (facing) -> facing != Direction.DOWN);
        ENABLED = BlockStateProperties.ENABLED;

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

    public MapCodec<XtremeHupperBlock> codec() {
        return CODEC;
    }

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static XtremeHupperBlock create(Properties settings) {
        return new XtremeHupperBlock(8, "default") {
        };
    }

    public XtremeHupperBlock(int cooldownInTicks, String translationKey) {
        this(cooldownInTicks, translationKey, false);
    }

    public XtremeHupperBlock(int cooldownInTicks, String translationKey, boolean withFilter) {
        super(
            Properties.ofFullCopy(Blocks.HOPPER)
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 4.8F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .setId(REGISTRY_HELPER.makeBlockRegistryKey(translationKey))
        );

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = translationKey;
        this.withFilter = withFilter;

        this.registerDefaultState((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(ENABLED, true));
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch ((Direction) state.getValue(FACING)) {
            case UP -> {
                return UP_SHAPE;
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
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        switch ((Direction) state.getValue(FACING)) {
            case UP -> {
                return UP_RAYCAST_SHAPE;
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
        return (BlockState) ((BlockState) this.defaultBlockState().setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.UP : direction)).setValue(ENABLED, true);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XtremeHupperBlockEntity(pos, state, cooldownInTicks, withFilter);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, XTREME_HUPPER_BLOCK_ENTITY.get(), XtremeHupperBlockEntity::serverTick);
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
            if (blockEntity instanceof XtremeHupperBlockEntity) {
                player.openMenu((XtremeHupperBlockEntity) blockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(Level world, BlockPos pos, BlockState state) {
        if (baseKey.equals("copper_hupper")) {
            return;
        }

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
        return (BlockState) state.setValue(FACING, rotation.rotate((Direction) state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction) state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, ENABLED});
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof XtremeHupperBlockEntity) {
            XtremeHupperBlockEntity.onEntityCollided(world, pos, state, entity, (XtremeHupperBlockEntity) blockEntity);
        }

    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
