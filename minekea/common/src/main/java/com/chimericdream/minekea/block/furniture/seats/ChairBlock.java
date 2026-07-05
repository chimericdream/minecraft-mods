package com.chimericdream.minekea.block.furniture.seats;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.entities.SimpleSeatEntity;
import com.chimericdream.minekea.ModInfo;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ChairBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING;

    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SEAT_SHAPE;
    private static final VoxelShape[] LEG_SHAPES;
    private static final Map<String, VoxelShape> SEAT_BACKS;

    static {
        FACING = EnumProperty.create("facing", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

        SEAT_SHAPE = Block.box(2.0, 8.0, 2.0, 14.0, 10.0, 14.0);
        LEG_SHAPES = new VoxelShape[]{
            Block.box(2.0, 0.0, 2.0, 4.0, 8.0, 4.0), // north-west
            Block.box(12.0, 0.0, 2.0, 14.0, 8.0, 4.0), // north-east
            Block.box(12.0, 0.0, 12.0, 14.0, 8.0, 14.0), // south-east
            Block.box(2.0, 0.0, 12.0, 4.0, 8.0, 14.0) // south-west
        };
        SEAT_BACKS = Map.of(
            "north", Block.box(2.0, 10.0, 2.0, 14.0, 22.0, 4.0),
            "south", Block.box(2.0, 10.0, 12.0, 14.0, 22.0, 14.0),
            "east", Block.box(12.0, 10.0, 2.0, 14.0, 22.0, 14.0),
            "west", Block.box(2.0, 10.0, 2.0, 4.0, 22.0, 14.0)
        );
    }

    public ChairBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/seating/chairs/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = Direction.NORTH;
        if (ctx.getPlayer() != null) {
            dir = ctx.getPlayer().getDirection();
        }

        return this.defaultBlockState()
            .setValue(FACING, dir)
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

    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        List<SimpleSeatEntity> seats = world.getEntitiesOfClass(SimpleSeatEntity.class, new AABB(pos), (Object) -> true);

        if (seats.isEmpty() && player.isShiftKeyDown()) {
            world.setBlockAndUpdate(pos, state.setValue(FACING, state.getValue(FACING).getClockWise()));

            return InteractionResult.SUCCESS;
        }

        if (seats.isEmpty()) {
            Entity seat = Seats.SEAT_ENTITY.get().create(world, EntitySpawnReason.EVENT);
            Vec3 seatPos = new Vec3(hit.getBlockPos().getX() + 0.5d, hit.getBlockPos().getY() - 1.15d, hit.getBlockPos().getZ() + 0.5d);

            if (seat == null) {
                return InteractionResult.PASS;
            }

            seat.absSnapTo(seatPos.x(), seatPos.y(), seatPos.z());
            world.addFreshEntity(seat);
            player.startRiding(seat);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        if (facing == Direction.SOUTH) {
            return Shapes.or(Shapes.or(SEAT_SHAPE, LEG_SHAPES), SEAT_BACKS.get("north"));
        }

        if (facing == Direction.EAST) {
            return Shapes.or(Shapes.or(SEAT_SHAPE, LEG_SHAPES), SEAT_BACKS.get("west"));
        }

        if (facing == Direction.WEST) {
            return Shapes.or(Shapes.or(SEAT_SHAPE, LEG_SHAPES), SEAT_BACKS.get("east"));
        }

        return Shapes.or(Shapes.or(SEAT_SHAPE, LEG_SHAPES), SEAT_BACKS.get("south"));
    }
}
