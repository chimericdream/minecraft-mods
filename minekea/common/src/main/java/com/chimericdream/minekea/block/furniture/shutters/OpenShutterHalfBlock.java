package com.chimericdream.minekea.block.furniture.shutters;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class OpenShutterHalfBlock extends Block implements Waterloggable {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;
    protected final BlockSetType blockSetType;

    public static final EnumProperty<ShutterHalf> HALF;
    public static final DirectionProperty WALL_SIDE;
    public static final BooleanProperty WATERLOGGED;

    private static final Map<String, VoxelShape> OUTLINE_LEFT;
    private static final Map<String, VoxelShape> OUTLINE_RIGHT;

    static {
        HALF = EnumProperty.of("half", ShutterHalf.class);
        WALL_SIDE = DirectionProperty.of("wall_side", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        WATERLOGGED = Properties.WATERLOGGED;

        OUTLINE_LEFT = Map.of(
            "north", Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            "south", Block.createCuboidShape(0.0, 0.0, 14.0, 8.0, 16.0, 16.0),
            "east", Block.createCuboidShape(14.0, 0.0, 8.0, 16.0, 16.0, 16.0),
            "west", Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 8.0)
        );

        OUTLINE_RIGHT = Map.of(
            "north", Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 2.0),
            "south", Block.createCuboidShape(8.0, 0.0, 14.0, 16.0, 16.0, 16.0),
            "east", Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 8.0),
            "west", Block.createCuboidShape(0.0, 0.0, 8.0, 2.0, 16.0, 16.0)
        );
    }

    public OpenShutterHalfBlock(BlockSetType type, BlockConfig config) {
        super(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS));

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(HALF, ShutterHalf.LEFT)
                .with(WALL_SIDE, Direction.NORTH)
                .with(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.blockSetType = type;
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("furniture/shutters/%s_open", material));
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(Shutters.SHUTTER_BLOCKS.get(config.getMaterial()).get());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, WALL_SIDE, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == ShutterHalf.LEFT) {
            return switch (state.get(WALL_SIDE)) {
                case SOUTH -> OUTLINE_LEFT.get("south");
                case WEST -> OUTLINE_LEFT.get("west");
                case EAST -> OUTLINE_LEFT.get("east");
                default -> OUTLINE_LEFT.get("north");
            };
        }

        return switch (state.get(WALL_SIDE)) {
            case SOUTH -> OUTLINE_RIGHT.get("south");
            case WEST -> OUTLINE_RIGHT.get("west");
            case EAST -> OUTLINE_RIGHT.get("east");
            default -> OUTLINE_RIGHT.get("north");
        };
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos centerPos;
        BlockPos oppositePos;

        ShutterHalf half = state.get(HALF);
        switch (state.get(WALL_SIDE)) {
            case SOUTH -> {
                centerPos = half == ShutterHalf.LEFT ? pos.west() : pos.east();
                oppositePos = half == ShutterHalf.LEFT ? pos.west(2) : pos.east(2);
            }
            case EAST -> {
                centerPos = half == ShutterHalf.LEFT ? pos.south() : pos.north();
                oppositePos = half == ShutterHalf.LEFT ? pos.south(2) : pos.north(2);
            }
            case WEST -> {
                centerPos = half == ShutterHalf.LEFT ? pos.north() : pos.south();
                oppositePos = half == ShutterHalf.LEFT ? pos.north(2) : pos.south(2);
            }
            default -> {
                centerPos = half == ShutterHalf.LEFT ? pos.east() : pos.west();
                oppositePos = half == ShutterHalf.LEFT ? pos.east(2) : pos.west(2);
            }
        }

        BlockState centerState = world.getBlockState(centerPos);
        if (centerState.getProperties().contains(Properties.WATERLOGGED) && centerState.get(WATERLOGGED)) {
            world.setBlockState(centerPos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(centerPos, Blocks.AIR.getDefaultState());
        }

        BlockState oppositeState = world.getBlockState(oppositePos);

        if (oppositeState.getProperties().contains(Properties.WATERLOGGED) && oppositeState.get(WATERLOGGED)) {
            world.setBlockState(oppositePos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(oppositePos, Blocks.AIR.getDefaultState());
        }

        if (state.get(WATERLOGGED)) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if (centerState.get(WATERLOGGED)) {
            world.scheduleFluidTick(centerPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        ItemEntity itemEntity = new ItemEntity(
            world,
            (double) pos.getX() + 0.5D,
            (double) pos.getY() + 0.5D,
            (double) pos.getZ() + 0.5D,
            Shutters.SHUTTER_BLOCKS.get(config.getMaterial()).get().asItem().getDefaultStack()
        );

        itemEntity.setToDefaultPickupDelay();

        world.spawnEntity(itemEntity);

        return super.onBreak(world, centerPos, centerState, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockPos centerPos;
        BlockPos oppositePos;

        ShutterHalf half = state.get(HALF);
        switch (state.get(WALL_SIDE)) {
            case SOUTH -> {
                centerPos = half == ShutterHalf.LEFT ? pos.west() : pos.east();
                oppositePos = half == ShutterHalf.LEFT ? pos.west(2) : pos.east(2);
            }
            case EAST -> {
                centerPos = half == ShutterHalf.LEFT ? pos.south() : pos.north();
                oppositePos = half == ShutterHalf.LEFT ? pos.south(2) : pos.north(2);
            }
            case WEST -> {
                centerPos = half == ShutterHalf.LEFT ? pos.north() : pos.south();
                oppositePos = half == ShutterHalf.LEFT ? pos.north(2) : pos.south(2);
            }
            default -> {
                centerPos = half == ShutterHalf.LEFT ? pos.east() : pos.west();
                oppositePos = half == ShutterHalf.LEFT ? pos.east(2) : pos.west(2);
            }
        }

        BlockState centerState = world.getBlockState(centerPos);
        centerState = centerState.cycle(ShutterBlock.OPEN);
        world.setBlockState(centerPos, centerState, 2);

        BlockState oppositeState = world.getBlockState(oppositePos);

        if (oppositeState.getProperties().contains(Properties.WATERLOGGED) && oppositeState.get(WATERLOGGED)) {
            world.setBlockState(oppositePos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(oppositePos, Blocks.AIR.getDefaultState());
        }

        if (state.get(WATERLOGGED)) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if (centerState.get(WATERLOGGED)) {
            world.scheduleFluidTick(centerPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        this.playToggleSound(player, world, centerPos);
        return ActionResult.success(world.isClient);
    }

    protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos) {
        world.playSound(player, pos, this.blockSetType.trapdoorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.emitGameEvent(player, GameEvent.BLOCK_CLOSE, pos);
    }

    public enum ShutterHalf implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        ShutterHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String asString() {
            return this.name;
        }
    }
}
