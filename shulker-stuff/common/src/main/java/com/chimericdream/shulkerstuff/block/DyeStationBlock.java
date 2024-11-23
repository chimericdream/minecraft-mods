package com.chimericdream.shulkerstuff.block;

import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DyeStationBlock extends BlockWithEntity {
    public static final MapCodec<DyeStationBlock> CODEC = createCodec(DyeStationBlock::create);

    private static final VoxelShape DEFAULT_SHAPE;

    static {
        DEFAULT_SHAPE = VoxelShapes.union(
            // front left leg
            Block.createCuboidShape(0.5, 0, 0.5, 2.5, 10, 2.5),
            // front right leg
            Block.createCuboidShape(13.5, 0, 0.5, 15.5, 10, 2.5),
            // back left leg
            Block.createCuboidShape(0.5, 0, 13.5, 2.5, 10, 15.5),
            // back right leg
            Block.createCuboidShape(13.5, 0, 13.5, 15.5, 10, 15.5),
            // table surface
            Block.createCuboidShape(0, 10, 0, 16, 12, 16),
            // lip
            Block.createCuboidShape(0.25, 11.5, 0.25, 15.75, 12.5, 1.25),
            Block.createCuboidShape(0.25, 11.5, 14.75, 15.75, 12.5, 15.75),
            Block.createCuboidShape(0.25, 11.5, 1.25, 1.25, 12.5, 14.75),
            Block.createCuboidShape(14.75, 11.5, 1.25, 15.75, 12.5, 14.75)
        );
    }

    static DyeStationBlock create(Settings settings) {
        return new DyeStationBlock() {
        };
    }

    public DyeStationBlock() {
        super(AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE));
    }

    public MapCodec<DyeStationBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyeStationBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.DYE_STATION_BLOCK_ENTITY.get(), DyeStationBlockEntity::tick);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT_SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return DEFAULT_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        NamedScreenHandlerFactory screenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);

        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
            return ActionResult.CONSUME;
        }

        return ActionResult.FAIL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DyeStationBlockEntity) {
                ItemScatterer.spawn(world, pos, (DyeStationBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
