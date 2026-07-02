package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.tag.CommonTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

abstract public class AbstractMultiHopperBlock extends BaseEntityBlock {
    public static final BooleanProperty ENABLED;
    public static final BooleanProperty NORTH_CONNECTED;
    public static final BooleanProperty SOUTH_CONNECTED;
    public static final BooleanProperty EAST_CONNECTED;
    public static final BooleanProperty WEST_CONNECTED;
    public static final BooleanProperty DOWN_CONNECTED;

    public static final VoxelShape TOP_SHAPE;
    public static final VoxelShape MIDDLE_SHAPE;
    public static final VoxelShape OUTSIDE_SHAPE;
    public static final VoxelShape INSIDE_SHAPE;

    public static final VoxelShape DEFAULT_SHAPE;
    public static final VoxelShape DOWN_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;
    public static final VoxelShape WEST_SHAPE;

    public static final VoxelShape DOWN_RAYCAST_SHAPE;
    public static final VoxelShape EAST_RAYCAST_SHAPE;
    public static final VoxelShape NORTH_RAYCAST_SHAPE;
    public static final VoxelShape SOUTH_RAYCAST_SHAPE;
    public static final VoxelShape WEST_RAYCAST_SHAPE;

    protected AbstractMultiHopperBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(NORTH_CONNECTED, false)
                .setValue(SOUTH_CONNECTED, false)
                .setValue(EAST_CONNECTED, false)
                .setValue(WEST_CONNECTED, false)
                .setValue(DOWN_CONNECTED, false)
                .setValue(ENABLED, true)
        );
    }

    private BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_CONNECTED;
            case SOUTH -> SOUTH_CONNECTED;
            case EAST -> EAST_CONNECTED;
            case WEST -> WEST_CONNECTED;
            default -> DOWN_CONNECTED;
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
        if (state.getValue(DOWN_CONNECTED)) {
            parts.add(DOWN_SHAPE);
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
        if (state.getValue(DOWN_CONNECTED)) {
            parts.add(DOWN_RAYCAST_SHAPE);
        }

        return Shapes.or(INSIDE_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState();
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
        } else if (y < LOWER_ARM_END) {
            connection = DOWN_CONNECTED;
        } else if (z < LOWER_ARM_END) {
            connection = NORTH_CONNECTED;
        }

        world.setBlockAndUpdate(pos, state.setValue(connection, !state.getValue(connection)));
        world.blockEntityChanged(pos);

        return InteractionResult.CONSUME;
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
        builder.add(ENABLED, NORTH_CONNECTED, SOUTH_CONNECTED, EAST_CONNECTED, WEST_CONNECTED, DOWN_CONNECTED);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    static {
        ENABLED = BlockStateProperties.ENABLED;
        NORTH_CONNECTED = BooleanProperty.create("north_connected");
        SOUTH_CONNECTED = BooleanProperty.create("south_connected");
        EAST_CONNECTED = BooleanProperty.create("east_connected");
        WEST_CONNECTED = BooleanProperty.create("west_connected");
        DOWN_CONNECTED = BooleanProperty.create("down_connected");

        TOP_SHAPE = Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        MIDDLE_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = Shapes.or(MIDDLE_SHAPE, TOP_SHAPE);
        INSIDE_SHAPE = box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);

        DEFAULT_SHAPE = Shapes.join(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanOp.ONLY_FIRST);
        DOWN_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = Shapes.or(DEFAULT_SHAPE, Block.box(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));

        DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAYCAST_SHAPE = Shapes.or(INSIDE_SHAPE, Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
