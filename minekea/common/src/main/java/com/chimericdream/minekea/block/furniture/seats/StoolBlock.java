package com.chimericdream.minekea.block.furniture.seats;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.entities.SimpleSeatEntity;
import com.chimericdream.minekea.ModInfo;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
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

public class StoolBlock extends Block implements SimpleWaterloggedBlock {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SEAT_SHAPE;
    private static final VoxelShape[] LEG_SHAPES;

    static {
        SEAT_SHAPE = Block.box(3.0, 6.0, 3.0, 13.0, 8.0, 13.0);
        LEG_SHAPES = new VoxelShape[]{
            Block.box(4.0, 0.0, 4.0, 6.0, 6.0, 6.0), // north-west
            Block.box(10.0, 0.0, 4.0, 12.0, 6.0, 6.0), // north-east
            Block.box(10.0, 0.0, 10.0, 12.0, 6.0, 12.0), // south-east
            Block.box(4.0, 0.0, 10.0, 6.0, 6.0, 12.0) // south-west
        };
    }

    public StoolBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/seating/stools/%s", material));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
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

        if (!seats.isEmpty()) {
            return InteractionResult.PASS;
        }

        Entity seat = Seats.SEAT_ENTITY.get().create(world, EntitySpawnReason.EVENT);
        Vec3 seatPos = new Vec3(hit.getBlockPos().getX() + 0.5d, hit.getBlockPos().getY() - 1.25d, hit.getBlockPos().getZ() + 0.5d);

        if (seat == null) {
            return  InteractionResult.PASS;
        }

        seat.absSnapTo(seatPos.x(), seatPos.y(), seatPos.z());
        world.addFreshEntity(seat);
        player.startRiding(seat);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(SEAT_SHAPE, LEG_SHAPES);
    }
}
