package com.chimericdream.minekea.block.furniture.armoires;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.furniture.ArmoireBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ArmoireBlock extends BlockWithEntity {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static final DirectionProperty FACING;

    protected static final VoxelShape NORTH_TOP_SHAPE;
    protected static final VoxelShape NORTH_BOTTOM_SHAPE;

    protected static final VoxelShape SOUTH_TOP_SHAPE;
    protected static final VoxelShape SOUTH_BOTTOM_SHAPE;

    protected static final VoxelShape EAST_TOP_SHAPE;
    protected static final VoxelShape EAST_BOTTOM_SHAPE;

    protected static final VoxelShape WEST_TOP_SHAPE;
    protected static final VoxelShape WEST_BOTTOM_SHAPE;

    static {
        FACING = HorizontalFacingBlock.FACING;
        HALF = Properties.DOUBLE_BLOCK_HALF;

        NORTH_TOP_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(15.00, 0.00, 0.00, 16.00, 16.00, 12.00),   // left
            Block.createCuboidShape(0.00, 0.00, 0.00, 1.00, 16.00, 12.00),     // right
            Block.createCuboidShape(1.00, 15.00, 1.00, 15.00, 16.00, 12.00),   // back
            Block.createCuboidShape(1.00, 0.00, 0.00, 15.00, 16.00, 1.00),     // top
            Block.createCuboidShape(1.00, 12.00, 5.00, 15.00, 13.00, 6.00)     // pole
        );
        NORTH_BOTTOM_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.00, 0.00, 0.00, 16.00, 1.00, 12.00),     // bottom
            Block.createCuboidShape(15.00, 1.00, 0.00, 16.00, 16.00, 12.00),   // ???
            Block.createCuboidShape(0.00, 1.00, 0.00, 1.00, 16.00, 12.00),     // right
            Block.createCuboidShape(1.00, 1.00, 0.00, 15.00, 16.00, 1.00),     // left
            Block.createCuboidShape(1.00, 7.00, 1.00, 15.00, 8.00, 9.00)       // shelf
        );

        SOUTH_TOP_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.00, 0.00, 4.00, 1.00, 16.00, 16.00),     // left
            Block.createCuboidShape(15.00, 0.00, 4.00, 16.00, 16.00, 16.00),   // right
            Block.createCuboidShape(1.00, 15.00, 4.00, 15.00, 16.00, 15.00),   // back
            Block.createCuboidShape(1.00, 0.00, 15.00, 15.00, 16.00, 16.00),   // top
            Block.createCuboidShape(1.00, 12.00, 10.00, 15.00, 13.00, 11.00)   // pole
        );
        SOUTH_BOTTOM_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.00, 0.00, 4.00, 16.00, 1.00, 16.00),     // bottom
            Block.createCuboidShape(1.00, 7.00, 7.00, 15.00, 8.00, 15.00),     // shelf
            Block.createCuboidShape(0.00, 1.00, 4.00, 1.00, 16.00, 16.00),     // right
            Block.createCuboidShape(15.00, 1.00, 4.00, 16.00, 16.00, 16.00),   // left
            Block.createCuboidShape(1.00, 1.00, 15.00, 15.00, 16.00, 16.00)    // back
        );

        EAST_TOP_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.00, 0.00, 15.00, 16.00, 16.00, 16.00),   // left
            Block.createCuboidShape(4.00, 0.00, 0.00, 16.00, 16.00, 1.00),     // right
            Block.createCuboidShape(4.00, 15.00, 1.00, 15.00, 16.00, 15.00),   // back
            Block.createCuboidShape(15.00, 0.00, 1.00, 16.00, 16.00, 15.00),   // top
            Block.createCuboidShape(10.00, 12.00, 1.00, 11.00, 13.00, 15.00)   // pole
        );
        EAST_BOTTOM_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.00, 0.00, 0.00, 16.00, 1.00, 16.00),     // bottom
            Block.createCuboidShape(4.00, 1.00, 15.00, 16.00, 16.00, 16.00),   // ???
            Block.createCuboidShape(4.00, 1.00, 0.00, 16.00, 16.00, 1.00),     // right
            Block.createCuboidShape(15.00, 1.00, 1.00, 16.00, 16.00, 15.00),   // left
            Block.createCuboidShape(7.00, 7.00, 1.00, 15.00, 8.00, 15.00)      // shelf
        );

        WEST_TOP_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.00, 0.00, 0.00, 12.00, 16.00, 1.00),     // left
            Block.createCuboidShape(0.00, 0.00, 15.00, 12.00, 16.00, 16.00),   // right
            Block.createCuboidShape(1.00, 15.00, 1.00, 12.00, 16.00, 15.00),   // back
            Block.createCuboidShape(0.00, 0.00, 1.00, 1.00, 16.00, 15.00),     // top
            Block.createCuboidShape(5.00, 12.00, 1.00, 6.00, 13.00, 15.00)     // pole
        );
        WEST_BOTTOM_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.00, 0.00, 0.00, 12.00, 1.00, 16.00),     // bottom
            Block.createCuboidShape(0.00, 1.00, 0.00, 12.00, 16.00, 1.00),     // ???
            Block.createCuboidShape(0.00, 1.00, 15.00, 12.00, 16.00, 16.00),   // right
            Block.createCuboidShape(0.00, 1.00, 1.00, 1.00, 16.00, 15.00),     // left
            Block.createCuboidShape(1.00, 7.00, 1.00, 9.0, 8.00, 15.00)       // shelf
        );
    }

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public ArmoireBlock(BlockConfig config) {
        super(AbstractBlock.Settings.copy(config.getIngredient("planks")));

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("furniture/armoires/%s", material));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            DoubleBlockHalf half = state.get(HALF);

            if (half == DoubleBlockHalf.UPPER) {
                BlockPos blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);

                if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockState2 = blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 35);
                    world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction lookDirection = ctx.getPlayerLookDirection();

        if (lookDirection.getAxis().isVertical()) {
            lookDirection = ctx.getHorizontalPlayerFacing();
        }

        return this.getDefaultState().with(FACING, lookDirection);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.isOf(this) && neighborState.get(HALF) != doubleBlockHalf ? state.with(FACING, neighborState.get(FACING)) : Blocks.AIR.getDefaultState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        BlockPos above = pos.up();
        BlockState aboveState = world.getBlockState(above);

        return state.get(HALF) == DoubleBlockHalf.LOWER ? aboveState.isAir() : belowState.isOf(this);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        DoubleBlockHalf half = state.get(HALF);

        return switch (facing) {
            case EAST -> half == DoubleBlockHalf.LOWER ? EAST_BOTTOM_SHAPE : EAST_TOP_SHAPE;
            case SOUTH -> half == DoubleBlockHalf.LOWER ? SOUTH_BOTTOM_SHAPE : SOUTH_TOP_SHAPE;
            case WEST -> half == DoubleBlockHalf.LOWER ? WEST_BOTTOM_SHAPE : WEST_TOP_SHAPE;
            default -> half == DoubleBlockHalf.LOWER ? NORTH_BOTTOM_SHAPE : NORTH_TOP_SHAPE;
        };
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.IGNORE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }

        return new ArmoireBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /*
     * Slot arrangement:
     * 0  - left: chestplate
     * 1  - left: leggings
     * 2  - left: helmet
     * 3  - left: boots
     * 4  - middle-left: chestplate
     * 5  - middle-left: leggings
     * 6  - middle-left: helmet
     * 7  - middle-left: boots
     * 8  - middle-right: chestplate
     * 9  - middle-right: leggings
     * 10 - middle-right: helmet
     * 11 - middle-right: boots
     * 12 - right: chestplate
     * 13 - right: leggings
     * 14 - right: helmet
     * 15 - right: boots
     */
    private int getTargetSlot(BlockState state, BlockHitResult hit) {
        Direction facing = state.get(FACING);
        DoubleBlockHalf half = state.get(HALF);
        Vec3d localCoords = hit.getPos().subtract(Vec3d.of(hit.getBlockPos()));

        int targetStand = 0; // 0, 1, 2, 3 -> left, middle-left, middle-right, right
        int targetItem = -1; // -1, 0, 1, 2, 3 -> none, chestplate, leggings, helmet, boots

        double x = localCoords.getX();
        double y = localCoords.getY();
        double z = localCoords.getZ();

        /*
         * East
         * z 0.06250 -> 0.28125 (far left)
         * z 0.28126 -> 0.50000 (middle left)
         * z 0.50001 -> 0.71875 (middle right)
         * z 0.71876 -> 0.93750 (far right)
         *
         * West
         * z 0.06250 -> 0.28125 (far right)
         * z 0.28126 -> 0.50000 (middle right)
         * z 0.50001 -> 0.71875 (middle left)
         * z 0.71876 -> 0.93750 (far left)
         *
         * South
         * x 0.06250 -> 0.28125 (far right)
         * x 0.28126 -> 0.50000 (middle right)
         * x 0.50001 -> 0.71875 (middle left)
         * x 0.71876 -> 0.93750 (far left)
         *
         * North
         * x 0.06250 -> 0.28125 (far left)
         * x 0.28126 -> 0.50000 (middle left)
         * x 0.50001 -> 0.71875 (middle right)
         * x 0.71876 -> 0.93750 (far right)
         *
         *
         *
         * Top
         * y  0.07501 -> 0.75000 (chestplate)
         * y -0.43125 -> 0.07500 (leggings)
         *
         * Bottom
         * y 0.56876 -> 1.0 (leggings)
         * y 0.25001 -> 0.56875 (helmet)
         * y 0.00001 -> 0.25000 (boots)
         */
        switch (facing) {
            case NORTH:
                if (x > 0.71875) {
                    targetStand = 3;
                } else if (x > 0.5) {
                    targetStand = 2;
                } else if (x > 0.28125) {
                    targetStand = 1;
                }
                break;
            case EAST:
                if (z > 0.71875) {
                    targetStand = 3;
                } else if (z > 0.5) {
                    targetStand = 2;
                } else if (z > 0.28125) {
                    targetStand = 1;
                }
                break;
            case SOUTH:
                if (x <= 0.28125) {
                    targetStand = 3;
                } else if (x <= 0.5) {
                    targetStand = 2;
                } else if (x <= 0.71875) {
                    targetStand = 1;
                }
                break;
            case WEST:
                if (z <= 0.28125) {
                    targetStand = 3;
                } else if (z <= 0.5) {
                    targetStand = 2;
                } else if (z <= 0.71875) {
                    targetStand = 1;
                }
                break;
        }

        if (half == DoubleBlockHalf.UPPER) {
            if (y <= 0.075) {
                targetItem = 1;
            } else if (y <= 0.75) {
                targetItem = 0;
            }
        } else {
            if (y > 0.56875) {
                targetItem = 1;
            } else if (y > 0.25) {
                targetItem = 2;
            } else {
                targetItem = 3;
            }
        }

        if (targetItem == -1) {
            return -1;
        }

        /*
         * stand	item	slot
         * 0    	0   	0
         * 0    	1   	1
         * 0    	2   	2
         * 0    	3   	3
         * 1    	0   	4
         * 1    	1   	5
         * 1    	2   	6
         * 1    	3   	7
         * 2    	0   	8
         * 2    	1   	9
         * 2    	2   	10
         * 2    	3   	11
         * 3    	0   	12
         * 3    	1   	13
         * 3    	2   	14
         * 3    	3   	15
         */
        return (targetStand * 3) + targetStand + targetItem;
    }

    private void logTargetSlot(int slot) {
        switch (slot) {
            case 0 -> MinekeaMod.LOGGER.info("Target slot: left: chestplate");
            case 1 -> MinekeaMod.LOGGER.info("Target slot: left: leggings");
            case 2 -> MinekeaMod.LOGGER.info("Target slot: left: helmet");
            case 3 -> MinekeaMod.LOGGER.info("Target slot: left: boots");
            case 4 -> MinekeaMod.LOGGER.info("Target slot: middle-left: chestplate");
            case 5 -> MinekeaMod.LOGGER.info("Target slot: middle-left: leggings");
            case 6 -> MinekeaMod.LOGGER.info("Target slot: middle-left: helmet");
            case 7 -> MinekeaMod.LOGGER.info("Target slot: middle-left: boots");
            case 8 -> MinekeaMod.LOGGER.info("Target slot: middle-right: chestplate");
            case 9 -> MinekeaMod.LOGGER.info("Target slot: middle-right: leggings");
            case 10 -> MinekeaMod.LOGGER.info("Target slot: middle-right: helmet");
            case 11 -> MinekeaMod.LOGGER.info("Target slot: middle-right: boots");
            case 12 -> MinekeaMod.LOGGER.info("Target slot: right: chestplate");
            case 13 -> MinekeaMod.LOGGER.info("Target slot: right: leggings");
            case 14 -> MinekeaMod.LOGGER.info("Target slot: right: helmet");
            case 15 -> MinekeaMod.LOGGER.info("Target slot: right: boots");
            default -> MinekeaMod.LOGGER.info("Target slot: none");
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ArmoireBlockEntity entity;

        try {
            if (state.get(HALF) == DoubleBlockHalf.UPPER) {
                entity = (ArmoireBlockEntity) world.getBlockEntity(pos.down());
            } else {
                entity = (ArmoireBlockEntity) world.getBlockEntity(pos);
            }

            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("The armoire at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));

            return ActionResult.FAIL;
        }

        int slot = getTargetSlot(state, hit);

        if (slot == -1) {
            return ActionResult.PASS;
        }

        if (!player.getMainHandStack().isEmpty()) {
            // Try to insert the item in the player's hand into the targeted slot in the armoire
            player.setStackInHand(Hand.MAIN_HAND, entity.tryInsert(slot, player.getStackInHand(Hand.MAIN_HAND)));
        } else if (player.isSneaking() && player.getMainHandStack().isEmpty()) {
            if (!entity.getStack(slot).isEmpty()) {
                ItemScatterer.spawn(
                    world,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    entity.removeStack(slot)
                );
            }
        }

        entity.markDirty();
        world.markDirty(pos);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ArmoireBlockEntity) {
                ItemScatterer.spawn(world, pos, (ArmoireBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
