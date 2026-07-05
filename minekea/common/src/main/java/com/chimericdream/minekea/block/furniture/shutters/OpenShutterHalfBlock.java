package com.chimericdream.minekea.block.furniture.shutters;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class OpenShutterHalfBlock extends Block implements SimpleWaterloggedBlock {
    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;
    protected final BlockSetType blockSetType;

    public static final EnumProperty<ShutterHalf> HALF;
    public static final EnumProperty<Direction> WALL_SIDE;
    public static final BooleanProperty WATERLOGGED;

    private static final Map<String, VoxelShape> OUTLINE_LEFT;
    private static final Map<String, VoxelShape> OUTLINE_RIGHT;

    static {
        HALF = EnumProperty.create("half", ShutterHalf.class);
        WALL_SIDE = EnumProperty.create("wall_side", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        WATERLOGGED = BlockStateProperties.WATERLOGGED;

        OUTLINE_LEFT = Map.of(
            "north", Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            "south", Block.box(0.0, 0.0, 14.0, 8.0, 16.0, 16.0),
            "east", Block.box(14.0, 0.0, 8.0, 16.0, 16.0, 16.0),
            "west", Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 8.0)
        );

        OUTLINE_RIGHT = Map.of(
            "north", Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 2.0),
            "south", Block.box(8.0, 0.0, 14.0, 16.0, 16.0, 16.0),
            "east", Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 8.0),
            "west", Block.box(0.0, 0.0, 8.0, 2.0, 16.0, 16.0)
        );
    }

    public OpenShutterHalfBlock(BlockSetType type, BlockConfig config) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(HALF, ShutterHalf.LEFT)
                .setValue(WALL_SIDE, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.blockSetType = type;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/shutters/%s_open", material));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Shutters.SHUTTER_BLOCKS.get(config.getMaterial()).get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WALL_SIDE, WATERLOGGED);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(HALF) == ShutterHalf.LEFT) {
            return switch (state.getValue(WALL_SIDE)) {
                case SOUTH -> OUTLINE_LEFT.get("south");
                case WEST -> OUTLINE_LEFT.get("west");
                case EAST -> OUTLINE_LEFT.get("east");
                default -> OUTLINE_LEFT.get("north");
            };
        }

        return switch (state.getValue(WALL_SIDE)) {
            case SOUTH -> OUTLINE_RIGHT.get("south");
            case WEST -> OUTLINE_RIGHT.get("west");
            case EAST -> OUTLINE_RIGHT.get("east");
            default -> OUTLINE_RIGHT.get("north");
        };
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
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockPos centerPos;
        BlockPos oppositePos;

        ShutterHalf half = state.getValue(HALF);
        switch (state.getValue(WALL_SIDE)) {
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
        if (centerState.getProperties().contains(BlockStateProperties.WATERLOGGED) && centerState.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(centerPos, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(centerPos, Blocks.AIR.defaultBlockState());
        }

        BlockState oppositeState = world.getBlockState(oppositePos);

        if (oppositeState.getProperties().contains(BlockStateProperties.WATERLOGGED) && oppositeState.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(oppositePos, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(oppositePos, Blocks.AIR.defaultBlockState());
        }

        if (state.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        if (centerState.getValue(WATERLOGGED)) {
            world.scheduleTick(centerPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if (!player.isCreative()) {
            ItemEntity itemEntity = new ItemEntity(
                world,
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.5D,
                Shutters.SHUTTER_BLOCKS.get(config.getMaterial()).get().asItem().getDefaultInstance()
            );

            itemEntity.setDefaultPickUpDelay();

            world.addFreshEntity(itemEntity);
        }

        return super.playerWillDestroy(world, centerPos, centerState, player);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos centerPos;
        BlockPos oppositePos;

        ShutterHalf half = state.getValue(HALF);
        switch (state.getValue(WALL_SIDE)) {
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
        world.setBlock(centerPos, centerState, 2);

        BlockState oppositeState = world.getBlockState(oppositePos);

        if (oppositeState.getProperties().contains(BlockStateProperties.WATERLOGGED) && oppositeState.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(oppositePos, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(oppositePos, Blocks.AIR.defaultBlockState());
        }

        if (state.getValue(WATERLOGGED)) {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        if (centerState.getValue(WATERLOGGED)) {
            world.scheduleTick(centerPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        this.playToggleSound(player, world, centerPos);
        return InteractionResult.SUCCESS;
    }

    protected void playToggleSound(@Nullable Player player, Level world, BlockPos pos) {
        world.playSound(player, pos, this.blockSetType.trapdoorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.gameEvent(player, GameEvent.BLOCK_CLOSE, pos);
    }

    public enum ShutterHalf implements StringRepresentable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        ShutterHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
