package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.tag.CommonTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractMultiHopperBlock extends BlockWithEntity {
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

    protected AbstractMultiHopperBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(NORTH_CONNECTED, false)
                .with(SOUTH_CONNECTED, false)
                .with(EAST_CONNECTED, false)
                .with(WEST_CONNECTED, false)
                .with(DOWN_CONNECTED, false)
                .with(ENABLED, true)
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
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.get(NORTH_CONNECTED)) {
            parts.add(NORTH_SHAPE);
        }
        if (state.get(SOUTH_CONNECTED)) {
            parts.add(SOUTH_SHAPE);
        }
        if (state.get(EAST_CONNECTED)) {
            parts.add(EAST_SHAPE);
        }
        if (state.get(WEST_CONNECTED)) {
            parts.add(WEST_SHAPE);
        }
        if (state.get(DOWN_CONNECTED)) {
            parts.add(DOWN_SHAPE);
        }

        return VoxelShapes.union(DEFAULT_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.get(NORTH_CONNECTED)) {
            parts.add(NORTH_RAYCAST_SHAPE);
        }
        if (state.get(SOUTH_CONNECTED)) {
            parts.add(SOUTH_RAYCAST_SHAPE);
        }
        if (state.get(EAST_CONNECTED)) {
            parts.add(EAST_RAYCAST_SHAPE);
        }
        if (state.get(WEST_CONNECTED)) {
            parts.add(WEST_RAYCAST_SHAPE);
        }
        if (state.get(DOWN_CONNECTED)) {
            parts.add(DOWN_RAYCAST_SHAPE);
        }

        return VoxelShapes.union(INSIDE_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    private double getPartialCoord(Direction hitSide, double coord) {
        double offset = 0.00001;

        if (hitSide == Direction.EAST || hitSide == Direction.SOUTH || hitSide == Direction.UP) {
            offset = -1 * offset;
        }

        int floor = MathHelper.floor(coord + offset);

        return coord - (double) floor;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Temporary workaround until the next version of Minekea adds its wrench to the common tag.
        if (!stack.isIn(CommonTags.WRENCHES) && !stack.getItem().getRegistryEntry().registryKey().getValue().equals(Identifier.of("minekea:tools/wrench"))) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }

        Direction hitSide = hit.getSide();
        Vec3d hitPos = hit.getPos();

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

        world.setBlockState(pos, state.with(connection, !state.get(connection)));
        world.markDirty(pos);

        return ActionResult.CONSUME;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENABLED, NORTH_CONNECTED, SOUTH_CONNECTED, EAST_CONNECTED, WEST_CONNECTED, DOWN_CONNECTED);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    static {
        ENABLED = Properties.ENABLED;
        NORTH_CONNECTED = BooleanProperty.of("north_connected");
        SOUTH_CONNECTED = BooleanProperty.of("south_connected");
        EAST_CONNECTED = BooleanProperty.of("east_connected");
        WEST_CONNECTED = BooleanProperty.of("west_connected");
        DOWN_CONNECTED = BooleanProperty.of("down_connected");

        TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
        INSIDE_SHAPE = createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);

        DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
        DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));

        DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
