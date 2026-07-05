package com.chimericdream.minekea.block.furniture.shelves;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.furniture.ShelfBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ShelfBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> WALL_SIDE;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    static {
        WALL_SIDE = EnumProperty.create("wall_side", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    public ShelfBlock(BlockConfig config) {
        this(config, makeId(config.getMaterial()));
    }

    public ShelfBlock(BlockConfig config, ResourceLocation blockId) {
        super(BlockBehaviour.Properties.ofFullCopy(config.getIngredient("planks")).setId(ResourceKey.create(Registries.BLOCK, blockId)));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(WALL_SIDE, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = blockId;
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/shelves/supported/%s", material));
    }

    @Override
    protected @NotNull MapCodec<ShelfBlock> codec() {
        return simpleCodec((_unused) -> new ShelfBlock(this.config));
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(WALL_SIDE, rotation.rotate(state.getValue(WALL_SIDE)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(WALL_SIDE)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WALL_SIDE, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction lookDirection = ctx.getNearestLookingDirection();

        if (lookDirection == Direction.DOWN || lookDirection == Direction.UP) {
            return this.defaultBlockState().setValue(WALL_SIDE, Direction.NORTH)
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
        }

        return this.defaultBlockState().setValue(WALL_SIDE, lookDirection.getOpposite())
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

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction wall = state.getValue(WALL_SIDE);

        return switch (wall) {
            case EAST -> Shapes.or(Block.box(0.0, 7.0, 0.0, 7.0, 9.0, 16.0),
                Block.box(0.0, 4.0, 0.0, 2.0, 7.0, 1.0),
                Block.box(2.0, 5.0, 15.0, 4.0, 7.0, 16.0),
                Block.box(2.0, 5.0, 0.0, 4.0, 7.0, 1.0),
                Block.box(4.0, 6.0, 0.0, 6.0, 7.0, 1.0),
                Block.box(4.0, 6.0, 15.0, 6.0, 7.0, 16.0),
                Block.box(0.0, 4.0, 15.0, 2.0, 7.0, 16.0)
            );
            case SOUTH -> Shapes.or(Block.box(0.0, 7.0, 0.0, 16.0, 9.0, 7.0),
                Block.box(15.0, 4.0, 0.0, 16.0, 7.0, 2.0),
                Block.box(0.0, 5.0, 2.0, 1.0, 7.0, 4.0),
                Block.box(15.0, 5.0, 2.0, 16.0, 7.0, 4.0),
                Block.box(15.0, 6.0, 4.0, 16.0, 7.0, 6.0),
                Block.box(0.0, 6.0, 4.0, 1.0, 7.0, 6.0),
                Block.box(0.0, 4.0, 0.0, 1.0, 7.0, 2.0)
            );
            case WEST -> Shapes.or(
                Block.box(9.0, 7.0, 0.0, 16.0, 9.0, 16.0),
                Block.box(14.0, 4.0, 15.0, 16.0, 7.0, 16.0),
                Block.box(12.0, 5.0, 0.0, 14.0, 7.0, 1.0),
                Block.box(12.0, 5.0, 15.0, 14.0, 7.0, 16.0),
                Block.box(10.0, 6.0, 15.0, 12.0, 7.0, 16.0),
                Block.box(10.0, 6.0, 0.0, 12.0, 7.0, 1.0),
                Block.box(14.0, 4.0, 0.0, 16.0, 7.0, 1.0)
            );
            default -> Shapes.or(
                Block.box(0.0, 7.0, 9.0, 16.0, 9.0, 16.0),
                Block.box(0.0, 4.0, 14.0, 1.0, 7.0, 16.0),
                Block.box(15.0, 5.0, 12.0, 16.0, 7.0, 14.0),
                Block.box(0.0, 5.0, 12.0, 1.0, 7.0, 14.0),
                Block.box(0.0, 6.0, 10.0, 1.0, 7.0, 12.0),
                Block.box(15.0, 6.0, 10.0, 16.0, 7.0, 12.0),
                Block.box(15.0, 4.0, 14.0, 16.0, 7.0, 16.0)
            );
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private int getTargetSlot(BlockState state, BlockHitResult hit) {
        Direction facing = state.getValue(WALL_SIDE);
        Vec3 localCoords = hit.getLocation().subtract(Vec3.atLowerCornerOf(hit.getBlockPos()));

        switch (facing) {
            case NORTH -> {
                double x = localCoords.x();
                if (x > 0.75) return 0;
                if (x > 0.50) return 1;
                if (x > 0.25) return 2;

                return 3;
            }
            case EAST -> {
                double z = localCoords.z();
                if (z > 0.75) return 0;
                if (z > 0.50) return 1;
                if (z > 0.25) return 2;

                return 3;
            }
            case SOUTH -> {
                double x = localCoords.x();
                if (x > 0.75) return 3;
                if (x > 0.50) return 2;
                if (x > 0.25) return 1;

                return 0;
            }
            case WEST -> {
                double z = localCoords.z();
                if (z > 0.75) return 3;
                if (z > 0.50) return 2;
                if (z > 0.25) return 1;

                return 0;
            }
        }

        return -1;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ShelfBlockEntity entity;

        try {
            entity = (ShelfBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error(String.format("The shelf at %s had an invalid block entity.\nBlock Entity: %s", pos, world.getBlockEntity(pos)));

            return InteractionResult.FAIL;
        }

        int slot = getTargetSlot(state, hit);
        if (slot == -1) {
            throw new IllegalStateException("It should not be possible to target a slot outside of the block");
        }

        if (!player.getMainHandItem().isEmpty()) {
            // Try to insert the item in the player's hand into the targeted slot on the shelf
            player.setItemInHand(InteractionHand.MAIN_HAND, entity.tryInsert(slot, player.getMainHandItem()));
        } else if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
            if (!entity.getItem(slot).isEmpty()) {
                Containers.dropItemStack(
                    world,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    entity.removeItemNoUpdate(slot)
                );
            }
        }

        entity.setChanged();
        world.blockEntityChanged(pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShelfBlockEntity) {
            Containers.dropContents(world, pos, (ShelfBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }
}
