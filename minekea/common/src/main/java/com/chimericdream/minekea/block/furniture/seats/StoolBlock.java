package com.chimericdream.minekea.block.furniture.seats;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.entities.SimpleSeatEntity;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;

public class StoolBlock extends Block implements Waterloggable {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SEAT_SHAPE;
    private static final VoxelShape[] LEG_SHAPES;

    static {
        SEAT_SHAPE = Block.createCuboidShape(3.0, 6.0, 3.0, 13.0, 8.0, 13.0);
        LEG_SHAPES = new VoxelShape[]{
            Block.createCuboidShape(4.0, 0.0, 4.0, 6.0, 6.0, 6.0), // north-west
            Block.createCuboidShape(10.0, 0.0, 4.0, 12.0, 6.0, 6.0), // north-east
            Block.createCuboidShape(10.0, 0.0, 10.0, 12.0, 6.0, 12.0), // south-east
            Block.createCuboidShape(4.0, 0.0, 10.0, 6.0, 6.0, 12.0) // south-west
        };
    }

    public StoolBlock(BlockConfig config) {
        super(config.getBaseSettings());

        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("furniture/seating/stools/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
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

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        List<SimpleSeatEntity> seats = world.getEntitiesByClass(SimpleSeatEntity.class, new Box(pos), (Object) -> true);

        if (!seats.isEmpty()) {
            return ActionResult.PASS;
        }

        Entity seat = Seats.SEAT_ENTITY.get().create(world);
        Vec3d seatPos = new Vec3d(hit.getBlockPos().getX() + 0.5d, hit.getBlockPos().getY() - 1.25d, hit.getBlockPos().getZ() + 0.5d);

        seat.updatePosition(seatPos.getX(), seatPos.getY(), seatPos.getZ());
        world.spawnEntity(seat);
        player.startRiding(seat);

        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(SEAT_SHAPE, LEG_SHAPES);
    }
}
