package com.chimericdream.minekea.block.containers.crates;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.client.screen.crate.DoubleCrateScreenHandler;
import com.chimericdream.minekea.entity.block.containers.CrateBlockEntity;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class CrateBlock extends BaseEntityBlock {
    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);

    public static final Integer ROW_COUNT = 6;

    public static final EnumProperty<Direction> FACING;
    public static final EnumProperty<Axis> AXIS;
    public static final BooleanProperty OPEN;

    public static final EnumProperty<ChestType> CRATE_TYPE;
    public static final BooleanProperty CONNECTED_NORTH;
    public static final BooleanProperty CONNECTED_SOUTH;
    public static final BooleanProperty CONNECTED_EAST;
    public static final BooleanProperty CONNECTED_WEST;

    public ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    private static final DoubleBlockCombiner.Combiner<CrateBlockEntity, Optional<Container>> INVENTORY_RETRIEVER;
    private static final DoubleBlockCombiner.Combiner<CrateBlockEntity, Optional<MenuProvider>> SCREEN_RETRIEVER;

    static {
        FACING = BlockStateProperties.FACING;
        AXIS = BlockStateProperties.AXIS;
        OPEN = BlockStateProperties.OPEN;

        CRATE_TYPE = BlockStateProperties.CHEST_TYPE;
        CONNECTED_NORTH = BooleanProperty.create("connected_north");
        CONNECTED_SOUTH = BooleanProperty.create("connected_south");
        CONNECTED_EAST = BooleanProperty.create("connected_east");
        CONNECTED_WEST = BooleanProperty.create("connected_west");

        INVENTORY_RETRIEVER = new DoubleBlockCombiner.Combiner<>() {
            public Optional<Container> getFromBoth(CrateBlockEntity crate1, CrateBlockEntity crate2) {
                return Optional.of(new CompoundContainer(crate1, crate2));
            }

            public Optional<Container> getFrom(CrateBlockEntity chestBlockEntity) {
                return Optional.of(chestBlockEntity);
            }

            public Optional<Container> acceptNone() {
                return Optional.empty();
            }
        };
        SCREEN_RETRIEVER = new DoubleBlockCombiner.Combiner<>() {
            public Optional<MenuProvider> getFromBoth(final CrateBlockEntity crate1, final CrateBlockEntity crate2) {
                final Container inventory = new CompoundContainer(crate1, crate2);

                return Optional.of(new MenuProvider() {
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        return new DoubleCrateScreenHandler(Crates.DOUBLE_CRATE_SCREEN_HANDLER.get(), i, playerInventory, inventory);
                    }

                    public Component getDisplayName() {
                        if (crate1.isTrapped()) {
                            return Component.translatable(DoubleCrateScreenHandler.TRAPPED_SCREEN_ID.toLanguageKey());
                        }

                        return Component.translatable(DoubleCrateScreenHandler.SCREEN_ID.toLanguageKey());
                    }
                });
            }

            public Optional<MenuProvider> getFrom(CrateBlockEntity crate) {
                return Optional.of(crate);
            }

            public Optional<MenuProvider> acceptNone() {
                return Optional.empty();
            }
        };
    }

    public CrateBlock(BlockBehaviour.Properties settings) {
        this(Crates.CONFIGS.get("oak"));
    }

    public CrateBlock(BlockConfig config) {
        this(config, makeId(config.getMaterial()));
    }

    public CrateBlock(BlockConfig config, ResourceLocation blockId) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).setId(REGISTRY_HELPER.makeBlockRegistryKey(blockId)));

        BLOCK_ID = blockId;
        this.config = config;

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(AXIS, Axis.Y)
                .setValue(FACING, Direction.NORTH)
                .setValue(CRATE_TYPE, ChestType.SINGLE)
                .setValue(CONNECTED_NORTH, false)
                .setValue(CONNECTED_SOUTH, false)
                .setValue(CONNECTED_EAST, false)
                .setValue(CONNECTED_WEST, false)
                .setValue(OPEN, false)
        );
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("containers/crates/%s", material));
    }

    @Override
    protected MapCodec<CrateBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(Crates.CRATE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, Crates.CRATE_BLOCK_ENTITY.get(), CrateBlockEntity::tick);
    }

    public static BlockState changeRotation(BlockState state, Rotation rotation) {
        if (isConnectedCrate(state)) {
            return state;
        }

        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> (BlockState) state.setValue(AXIS, Axis.Z);
                case Z -> (BlockState) state.setValue(AXIS, Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AXIS, OPEN, CRATE_TYPE, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_EAST, CONNECTED_WEST);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placementState = this.defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis());

        // if the player is sneaking, and they are targeting another block of this type, and that block is not already connected elsewhere
        if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown() && ctx.getClickedFace().getAxis().isHorizontal()) {
            Direction side = ctx.getClickedFace().getOpposite();
            BlockState neighbor = getAvailableNeighboringCrate(ctx, side);
            BooleanProperty prop = getConnectionProperty(side);

            if (neighbor != null) {
                return this.defaultBlockState().setValue(AXIS, Axis.Y).setValue(FACING, Direction.UP).setValue(prop, true).setValue(CRATE_TYPE, getDoubleCrateType(side));
            }
        }

        // if the player is sneaking, or if this crate is not being placed vertically, skip checking for double crate stuff
        if (ctx.isSecondaryUseActive() || ctx.getClickedFace().getAxis().isHorizontal()) {
            if (ctx.getClickedFace().getAxis().equals(Axis.X)) {
                return placementState.setValue(FACING, Direction.EAST);
            }

            return placementState.setValue(FACING, Direction.SOUTH);
        }

        BlockState neighbor;

        neighbor = getAvailableNeighboringCrate(ctx, Direction.NORTH);
        if (neighbor != null) {
            return placementState.setValue(CONNECTED_NORTH, true).setValue(FACING, Direction.UP).setValue(CRATE_TYPE, ChestType.RIGHT);
        }

        neighbor = getAvailableNeighboringCrate(ctx, Direction.EAST);
        if (neighbor != null) {
            return placementState.setValue(CONNECTED_EAST, true).setValue(FACING, Direction.UP).setValue(CRATE_TYPE, ChestType.LEFT);
        }

        neighbor = getAvailableNeighboringCrate(ctx, Direction.SOUTH);
        if (neighbor != null) {
            return placementState.setValue(CONNECTED_SOUTH, true).setValue(FACING, Direction.UP).setValue(CRATE_TYPE, ChestType.LEFT);
        }

        neighbor = getAvailableNeighboringCrate(ctx, Direction.WEST);
        if (neighbor != null) {
            return placementState.setValue(CONNECTED_WEST, true).setValue(FACING, Direction.UP).setValue(CRATE_TYPE, ChestType.RIGHT);
        }

        return placementState;
    }

    @Nullable
    private BooleanProperty getConnectionProperty(Direction dir) {
        return switch (dir) {
            case DOWN, UP -> null;
            case NORTH -> CONNECTED_NORTH;
            case SOUTH -> CONNECTED_SOUTH;
            case EAST -> CONNECTED_EAST;
            case WEST -> CONNECTED_WEST;
        };
    }

    @Nullable
    private ChestType getDoubleCrateType(Direction dir) {
        return switch (dir) {
            case DOWN, UP -> null;
            case NORTH, WEST -> ChestType.RIGHT;
            case SOUTH, EAST -> ChestType.LEFT;
        };
    }

    @Nullable
    private BooleanProperty getReverseConnectionProperty(Direction dir) {
        return switch (dir) {
            case DOWN, UP -> null;
            case NORTH -> CONNECTED_SOUTH;
            case SOUTH -> CONNECTED_NORTH;
            case EAST -> CONNECTED_WEST;
            case WEST -> CONNECTED_EAST;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(getConnectionProperty(direction)) && !neighborState.is(this)) {
            return state.setValue(getConnectionProperty(direction), false).setValue(CRATE_TYPE, ChestType.SINGLE);
        }

        if (direction.getAxis().isVertical() || !neighborState.is(this)) {
            return state;
        }

        BooleanProperty prop = getReverseConnectionProperty(direction);
        if (neighborState.getValue(prop)) {
            return state.setValue(getConnectionProperty(direction), true).setValue(FACING, Direction.UP);
        }

        return state;
    }

    private static boolean isConnectedCrate(BlockState crate) {
        return crate.getValue(CONNECTED_NORTH) || crate.getValue(CONNECTED_SOUTH) || crate.getValue(CONNECTED_EAST) || crate.getValue(CONNECTED_WEST);
    }

    @Nullable
    private BlockState getAvailableNeighboringCrate(BlockPlaceContext ctx, Direction dir) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(dir));

        if (!blockState.is(this) || isConnectedCrate(blockState) || blockState.getValue(AXIS).isHorizontal()) {
            return null;
        }

        return blockState;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        MenuProvider screenHandlerFactory = this.getMenuProvider(state, world, pos);

        if (screenHandlerFactory != null) {
            player.openMenu(screenHandlerFactory);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    public static Direction getFacing(BlockState state) {
        if (state.getValue(CONNECTED_NORTH)) {
            return Direction.NORTH;
        }

        if (state.getValue(CONNECTED_SOUTH)) {
            return Direction.SOUTH;
        }

        if (state.getValue(CONNECTED_EAST)) {
            return Direction.EAST;
        }

        if (state.getValue(CONNECTED_WEST)) {
            return Direction.WEST;
        }

        return state.getValue(FACING);
    }

    public static DoubleBlockCombiner.BlockType getDoubleBlockType(BlockState state) {
        if (!isConnectedCrate(state)) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        }

        if (state.getValue(CONNECTED_NORTH) || state.getValue(CONNECTED_EAST)) {
            return DoubleBlockCombiner.BlockType.FIRST;
        }

        return DoubleBlockCombiner.BlockType.SECOND;
    }

    public DoubleBlockCombiner.NeighborCombineResult<CrateBlockEntity> getBlockEntitySource(BlockState state, Level world, BlockPos pos) {
        return DoubleBlockCombiner.combineWithNeigbour(
            Crates.CRATE_BLOCK_ENTITY.get(),
            CrateBlock::getDoubleBlockType,
            CrateBlock::getFacing,
            FACING,
            state,
            world,
            pos,
            (worldx, posx) -> false
        );
    }

    @Nullable
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return this.getBlockEntitySource(state, world, pos).apply(SCREEN_RETRIEVER).orElse(null);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity) {
            Containers.dropContents(world, pos, (CrateBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }
}
