package com.chimericdream.minekea.block.furniture.displaycases;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.furniture.DisplayCaseBlockEntity;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DisplayCaseBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 8);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape MAIN_SHAPE;
    private static final VoxelShape BASEBOARD_SHAPE;

    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    static {
        MAIN_SHAPE = Block.box(0.0, 2.0, 0.0, 16.0, 16.0, 16.0);
        BASEBOARD_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    }

    public DisplayCaseBlock(BlockConfig config) {
        super(BlockBehaviour.Properties.ofFullCopy(config.getIngredient("planks")).setId(ResourceKey.create(Registries.BLOCK, makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(WATERLOGGED, false)
        );
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/display_cases/%s", material));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        DisplayCaseBlockEntity entity;

        try {
            entity = (DisplayCaseBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error(String.format("The display case at %s had an invalid block entity.\nBlock Entity: %s", pos, world.getBlockEntity(pos)));

            return InteractionResult.FAIL;
        }

        if (entity.isEmpty()) {
            // If the player is holding something, put it in the case
            if (!stack.isEmpty()) {
                ItemStack toInsert = stack.copy();
                toInsert.setCount(1);

                entity.setItem(0, toInsert);

                stack.shrink(1);

                world.setBlockAndUpdate(pos, state);
                entity.setChanged();
                entity.playAddItemSound();
            }

            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            // If the player is sneaking, get what's in the case
            Containers.dropItemStack(
                world,
                player.getX(),
                player.getY(),
                player.getZ(),
                entity.getItem(0).copy()
            );

            entity.clearContent();

            world.setBlockAndUpdate(pos, state.setValue(ROTATION, 0));
            entity.setChanged();
            entity.playRemoveItemSound();
        } else {
            // If the player isn't sneaking, or if they have an item in their hand, rotate the item in the case
            int rotation = state.getValue(ROTATION);
            int newRotation = rotation >= 7 ? 0 : rotation + 1;

            world.setBlockAndUpdate(pos, state.setValue(ROTATION, newRotation));
            entity.playRotateItemSound();
        }

        world.blockEntityChanged(pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DisplayCaseBlockEntity) {
            Containers.dropContents(world, pos, (DisplayCaseBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayCaseBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(MAIN_SHAPE, BASEBOARD_SHAPE);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        ImplementedInventory displayCase;

        try {
            displayCase = (ImplementedInventory) world.getBlockEntity(pos);
            assert displayCase != null;
        } catch (Exception e) {
            throw new IllegalStateException(String.format("The display case at %s had an invalid block entity.\nBlock Entity: %s", pos, world.getBlockEntity(pos)));
        }

        if (displayCase.isEmpty()) {
            return 0;
        }

        int rotation = state.getValue(ROTATION);

        return (rotation * 2) + 1;
    }
}
