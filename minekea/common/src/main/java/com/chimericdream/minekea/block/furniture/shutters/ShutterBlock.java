package com.chimericdream.minekea.block.furniture.shutters;


import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ShutterBlock extends Block implements SimpleWaterloggedBlock {
    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;
    protected final BlockSetType blockSetType;

    public static final BooleanProperty OPEN;
    public static final BooleanProperty POWERED;
    public static final EnumProperty<Direction> WALL_SIDE;
    public static final BooleanProperty WATERLOGGED;

    private static final Map<String, VoxelShape> OUTLINE_CLOSED;
    private static final Map<String, VoxelShape> HITBOX_OPEN;

    static {
        OPEN = BlockStateProperties.OPEN;
        POWERED = BlockStateProperties.POWERED;
        WALL_SIDE = EnumProperty.create("wall_side", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        WATERLOGGED = BlockStateProperties.WATERLOGGED;

        OUTLINE_CLOSED = Map.of(
            "north", Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            "south", Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0),
            "east", Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            "west", Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0)
        );
        HITBOX_OPEN = Map.of(
            "north", Shapes.or(
                Block.box(16.0, 0.0, 0.0, 24.0, 16.0, 2.0),
                Block.box(-8.0, 0.0, 0.0, 0.0, 16.0, 2.0)
            ),
            "south", Shapes.or(
                Block.box(16.0, 0.0, 14.0, 24.0, 16.0, 16.0),
                Block.box(-8.0, 0.0, 14.0, 0.0, 16.0, 16.0)
            ),
            "east", Shapes.or(
                Block.box(14.0, 0.0, -8.0, 16.0, 16.0, 0.0),
                Block.box(14.0, 0.0, 16.0, 16.0, 16.0, 24.0)
            ),
            "west", Shapes.or(
                Block.box(0.0, 0.0, -8.0, 2.0, 16.0, 0.0),
                Block.box(0.0, 0.0, 16.0, 2.0, 16.0, 24.0)
            )
        );
    }

    public ShutterBlock(BlockSetType type, BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(OPEN, false)
                .setValue(POWERED, false)
                .setValue(WALL_SIDE, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.blockSetType = type;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/shutters/%s", material));
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(OPEN)) {
            removeOpenHalves(state, world, pos);
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, POWERED, WALL_SIDE, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(OPEN);
            case WATER -> state.getValue(WATERLOGGED);
        };
    }

    private static boolean hasSpaceToOpen(BlockState state, BlockGetter world, BlockPos pos) {
        BlockPos pos1 = pos.east();
        BlockPos pos2 = pos.west();

        Direction wallSide = state.getValue(WALL_SIDE);
        if (wallSide == Direction.EAST || wallSide == Direction.WEST) {
            pos1 = pos.north();
            pos2 = pos.south();
        }

        BlockState pos1State = world.getBlockState(pos1);
        BlockState pos2State = world.getBlockState(pos2);

        return (pos1State.is(Blocks.AIR) || pos1State.is(Blocks.WATER))
            && (pos2State.is(Blocks.AIR) || pos2State.is(Blocks.WATER));
    }

    private void placeOpenHalves(BlockState state, Level world, BlockPos pos) {
        BlockPos left;
        BlockPos right;

        switch (state.getValue(WALL_SIDE)) {
            case NORTH -> {
                left = pos.west();
                right = pos.east();
            }
            case EAST -> {
                left = pos.north();
                right = pos.south();
            }
            case SOUTH -> {
                left = pos.east();
                right = pos.west();
            }
            default -> {
                left = pos.south();
                right = pos.north();
            }
        }

        BlockState leftState = world.getBlockState(left);
        BlockState rightState = world.getBlockState(right);

        BlockState baseNewState = Shutters.OPEN_SHUTTER_HALF_BLOCKS
            .get(config.getMaterial())
            .get()
            .defaultBlockState()
            .setValue(OpenShutterHalfBlock.WALL_SIDE, state.getValue(WALL_SIDE));

        BlockState newLeftState = baseNewState.setValue(OpenShutterHalfBlock.HALF, OpenShutterHalfBlock.ShutterHalf.LEFT);
        BlockState newRightState = baseNewState.setValue(OpenShutterHalfBlock.HALF, OpenShutterHalfBlock.ShutterHalf.RIGHT);

        if (leftState.is(Blocks.WATER)) {
            world.setBlockAndUpdate(
                left,
                newLeftState.setValue(OpenShutterHalfBlock.WATERLOGGED, true)
            );
        } else {
            world.setBlockAndUpdate(left, newLeftState);
        }

        if (rightState.is(Blocks.WATER)) {
            world.setBlockAndUpdate(right, newRightState.setValue(OpenShutterHalfBlock.WATERLOGGED, true));
        } else {
            world.setBlockAndUpdate(right, newRightState);
        }
    }

    private void removeOpenHalves(BlockState state, Level world, BlockPos pos) {
        BlockPos pos1 = pos.east();
        BlockPos pos2 = pos.west();

        Direction wallSide = state.getValue(WALL_SIDE);
        if (wallSide == Direction.EAST || wallSide == Direction.WEST) {
            pos1 = pos.north();
            pos2 = pos.south();
        }

        BlockState pos1State = world.getBlockState(pos1);
        BlockState pos2State = world.getBlockState(pos2);

        if (pos1State.getProperties().contains(BlockStateProperties.WATERLOGGED) && pos1State.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(pos1, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos1, Blocks.AIR.defaultBlockState());
        }

        if (pos2State.getProperties().contains(BlockStateProperties.WATERLOGGED) && pos2State.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(pos2, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos2, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        boolean isOpen = state.getValue(OPEN);

        if (!isOpen && !hasSpaceToOpen(state, world, pos)) {
            return InteractionResult.FAIL;
        }

        if (isOpen) {
            removeOpenHalves(state, world, pos);
        } else {
            placeOpenHalves(state, world, pos);
        }

        state = state.cycle(OPEN);
        world.setBlock(pos, state, 2);
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        this.playToggleSound(player, world, pos, state.getValue(OPEN));
        return InteractionResult.SUCCESS;
    }

    protected void playToggleSound(@Nullable Player player, Level world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if (!world.isClientSide()) {
            boolean isReceivingPower = world.hasNeighborSignal(pos);
            boolean isPowered = state.getValue(POWERED);
            boolean isOpen = state.getValue(OPEN);

            if (isReceivingPower != isPowered) {
                if (isOpen != isReceivingPower) {
                    if (isOpen) {
                        removeOpenHalves(state, world, pos);
                    } else {
                        placeOpenHalves(state, world, pos);
                    }

                    state = state.setValue(OPEN, isReceivingPower);
                    this.playToggleSound(null, world, pos, isReceivingPower);
                }

                world.setBlock(pos, state.setValue(POWERED, isReceivingPower), 2);
                if (state.getValue(WATERLOGGED)) {
                    world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());


        // Get the player's look direction and ignore vertical directions (UP and DOWN)
        Direction playerLookDirection = ctx.getNearestLookingDirection();
        if (playerLookDirection.getAxis().isVertical()) {
            playerLookDirection = ctx.getHorizontalDirection(); // Default to horizontal facing
        }

        BlockState blockState = this.defaultBlockState().setValue(WALL_SIDE, playerLookDirection);

        if (ctx.getLevel().hasNeighborSignal(ctx.getClickedPos())) {
            blockState = blockState.setValue(OPEN, true).setValue(POWERED, true);
        }

        return blockState.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return Shapes.empty();
        }

        return switch (state.getValue(WALL_SIDE)) {
            case SOUTH -> OUTLINE_CLOSED.get("south");
            case WEST -> OUTLINE_CLOSED.get("west");
            case EAST -> OUTLINE_CLOSED.get("east");
            default -> OUTLINE_CLOSED.get("north");
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return switch (state.getValue(WALL_SIDE)) {
                case SOUTH -> HITBOX_OPEN.get("south");
                case WEST -> HITBOX_OPEN.get("west");
                case EAST -> HITBOX_OPEN.get("east");
                default -> HITBOX_OPEN.get("north");
            };
        }

        return switch (state.getValue(WALL_SIDE)) {
            case SOUTH -> OUTLINE_CLOSED.get("south");
            case WEST -> OUTLINE_CLOSED.get("west");
            case EAST -> OUTLINE_CLOSED.get("east");
            default -> OUTLINE_CLOSED.get("north");
        };
    }
}
