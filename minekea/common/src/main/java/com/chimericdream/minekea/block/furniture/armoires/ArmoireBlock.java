package com.chimericdream.minekea.block.furniture.armoires;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.furniture.ArmoireBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ArmoireBlock extends BaseEntityBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static final EnumProperty<Direction> FACING;

    protected static final VoxelShape NORTH_TOP_SHAPE;
    protected static final VoxelShape NORTH_BOTTOM_SHAPE;

    protected static final VoxelShape SOUTH_TOP_SHAPE;
    protected static final VoxelShape SOUTH_BOTTOM_SHAPE;

    protected static final VoxelShape EAST_TOP_SHAPE;
    protected static final VoxelShape EAST_BOTTOM_SHAPE;

    protected static final VoxelShape WEST_TOP_SHAPE;
    protected static final VoxelShape WEST_BOTTOM_SHAPE;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

        NORTH_TOP_SHAPE = Shapes.or(
            Block.box(15.00, 0.00, 0.00, 16.00, 16.00, 12.00),   // left
            Block.box(0.00, 0.00, 0.00, 1.00, 16.00, 12.00),     // right
            Block.box(1.00, 15.00, 1.00, 15.00, 16.00, 12.00),   // back
            Block.box(1.00, 0.00, 0.00, 15.00, 16.00, 1.00),     // top
            Block.box(1.00, 12.00, 5.00, 15.00, 13.00, 6.00)     // pole
        );
        NORTH_BOTTOM_SHAPE = Shapes.or(
            Block.box(0.00, 0.00, 0.00, 16.00, 1.00, 12.00),     // bottom
            Block.box(15.00, 1.00, 0.00, 16.00, 16.00, 12.00),   // ???
            Block.box(0.00, 1.00, 0.00, 1.00, 16.00, 12.00),     // right
            Block.box(1.00, 1.00, 0.00, 15.00, 16.00, 1.00),     // left
            Block.box(1.00, 7.00, 1.00, 15.00, 8.00, 9.00)       // shelf
        );

        SOUTH_TOP_SHAPE = Shapes.or(
            Block.box(0.00, 0.00, 4.00, 1.00, 16.00, 16.00),     // left
            Block.box(15.00, 0.00, 4.00, 16.00, 16.00, 16.00),   // right
            Block.box(1.00, 15.00, 4.00, 15.00, 16.00, 15.00),   // back
            Block.box(1.00, 0.00, 15.00, 15.00, 16.00, 16.00),   // top
            Block.box(1.00, 12.00, 10.00, 15.00, 13.00, 11.00)   // pole
        );
        SOUTH_BOTTOM_SHAPE = Shapes.or(
            Block.box(0.00, 0.00, 4.00, 16.00, 1.00, 16.00),     // bottom
            Block.box(1.00, 7.00, 7.00, 15.00, 8.00, 15.00),     // shelf
            Block.box(0.00, 1.00, 4.00, 1.00, 16.00, 16.00),     // right
            Block.box(15.00, 1.00, 4.00, 16.00, 16.00, 16.00),   // left
            Block.box(1.00, 1.00, 15.00, 15.00, 16.00, 16.00)    // back
        );

        EAST_TOP_SHAPE = Shapes.or(
            Block.box(4.00, 0.00, 15.00, 16.00, 16.00, 16.00),   // left
            Block.box(4.00, 0.00, 0.00, 16.00, 16.00, 1.00),     // right
            Block.box(4.00, 15.00, 1.00, 15.00, 16.00, 15.00),   // back
            Block.box(15.00, 0.00, 1.00, 16.00, 16.00, 15.00),   // top
            Block.box(10.00, 12.00, 1.00, 11.00, 13.00, 15.00)   // pole
        );
        EAST_BOTTOM_SHAPE = Shapes.or(
            Block.box(4.00, 0.00, 0.00, 16.00, 1.00, 16.00),     // bottom
            Block.box(4.00, 1.00, 15.00, 16.00, 16.00, 16.00),   // ???
            Block.box(4.00, 1.00, 0.00, 16.00, 16.00, 1.00),     // right
            Block.box(15.00, 1.00, 1.00, 16.00, 16.00, 15.00),   // left
            Block.box(7.00, 7.00, 1.00, 15.00, 8.00, 15.00)      // shelf
        );

        WEST_TOP_SHAPE = Shapes.or(
            Block.box(0.00, 0.00, 0.00, 12.00, 16.00, 1.00),     // left
            Block.box(0.00, 0.00, 15.00, 12.00, 16.00, 16.00),   // right
            Block.box(1.00, 15.00, 1.00, 12.00, 16.00, 15.00),   // back
            Block.box(0.00, 0.00, 1.00, 1.00, 16.00, 15.00),     // top
            Block.box(5.00, 12.00, 1.00, 6.00, 13.00, 15.00)     // pole
        );
        WEST_BOTTOM_SHAPE = Shapes.or(
            Block.box(0.00, 0.00, 0.00, 12.00, 1.00, 16.00),     // bottom
            Block.box(0.00, 1.00, 0.00, 12.00, 16.00, 1.00),     // ???
            Block.box(0.00, 1.00, 15.00, 12.00, 16.00, 16.00),   // right
            Block.box(0.00, 1.00, 1.00, 1.00, 16.00, 15.00),     // left
            Block.box(1.00, 7.00, 1.00, 9.0, 8.00, 15.00)       // shelf
        );
    }

    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public ArmoireBlock(BlockConfig config) {
        super(BlockBehaviour.Properties.ofFullCopy(config.getIngredient("planks")).pushReaction(PushReaction.IGNORE).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
        );

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/armoires/%s", material));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && player.isCreative()) {
            DoubleBlockHalf half = state.getValue(HALF);

            if (half == DoubleBlockHalf.UPPER) {
                BlockPos blockPos = pos.below();
                BlockState blockState = world.getBlockState(blockPos);

                if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockState2 = blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    world.setBlock(blockPos, blockState2, 35);
                    world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
                }
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction lookDirection = ctx.getNearestLookingDirection();

        if (lookDirection.getAxis().isVertical()) {
            lookDirection = ctx.getHorizontalDirection();
        }

        return this.defaultBlockState().setValue(FACING, lookDirection);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);

        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf ? state.setValue(FACING, neighborState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = world.getBlockState(below);
        BlockPos above = pos.above();
        BlockState aboveState = world.getBlockState(above);

        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? aboveState.isAir() : belowState.is(this);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);

        return switch (facing) {
            case EAST -> half == DoubleBlockHalf.LOWER ? EAST_BOTTOM_SHAPE : EAST_TOP_SHAPE;
            case SOUTH -> half == DoubleBlockHalf.LOWER ? SOUTH_BOTTOM_SHAPE : SOUTH_TOP_SHAPE;
            case WEST -> half == DoubleBlockHalf.LOWER ? WEST_BOTTOM_SHAPE : WEST_TOP_SHAPE;
            default -> half == DoubleBlockHalf.LOWER ? NORTH_BOTTOM_SHAPE : NORTH_TOP_SHAPE;
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }

        return new ArmoireBlockEntity(pos, state);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((_unused) -> new ArmoireBlock(this.config));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
        Direction facing = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);
        Vec3 localCoords = hit.getLocation().subtract(Vec3.atLowerCornerOf(hit.getBlockPos()));

        int targetStand = 0; // 0, 1, 2, 3 -> left, middle-left, middle-right, right
        int targetItem = -1; // -1, 0, 1, 2, 3 -> none, chestplate, leggings, helmet, boots

        double x = localCoords.x();
        double y = localCoords.y();
        double z = localCoords.z();

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
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ArmoireBlockEntity entity;

        try {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                entity = (ArmoireBlockEntity) world.getBlockEntity(pos.below());
            } else {
                entity = (ArmoireBlockEntity) world.getBlockEntity(pos);
            }

            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("The armoire at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));

            return InteractionResult.FAIL;
        }

        int slot = getTargetSlot(state, hit);

        if (slot == -1) {
            return InteractionResult.PASS;
        }

        if (!player.getMainHandItem().isEmpty()) {
            // Try to insert the item in the player's hand into the targeted slot in the armoire
            player.setItemInHand(InteractionHand.MAIN_HAND, entity.tryInsert(slot, player.getItemInHand(InteractionHand.MAIN_HAND)));
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
        if (blockEntity instanceof ArmoireBlockEntity) {
            Containers.dropContents(world, pos, (ArmoireBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }
}
