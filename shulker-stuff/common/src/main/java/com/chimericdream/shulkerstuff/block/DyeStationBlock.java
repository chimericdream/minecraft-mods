package com.chimericdream.shulkerstuff.block;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class DyeStationBlock extends BaseEntityBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "dye_station");
    public static final MapCodec<DyeStationBlock> CODEC = simpleCodec(DyeStationBlock::create);

    private static final VoxelShape DEFAULT_SHAPE;

    static {
        DEFAULT_SHAPE = Shapes.or(
            // front left leg
            Block.box(0.5, 0, 0.5, 2.5, 10, 2.5),
            // front right leg
            Block.box(13.5, 0, 0.5, 15.5, 10, 2.5),
            // back left leg
            Block.box(0.5, 0, 13.5, 2.5, 10, 15.5),
            // back right leg
            Block.box(13.5, 0, 13.5, 15.5, 10, 15.5),
            // table surface
            Block.box(0, 10, 0, 16, 12, 16),
            // lip
            Block.box(0.25, 11.5, 0.25, 15.75, 12.5, 1.25),
            Block.box(0.25, 11.5, 14.75, 15.75, 12.5, 15.75),
            Block.box(0.25, 11.5, 1.25, 1.25, 12.5, 14.75),
            Block.box(14.75, 11.5, 1.25, 15.75, 12.5, 14.75)
        );
    }

    static DyeStationBlock create(BlockBehaviour.Properties settings) {
        return new DyeStationBlock() {
        };
    }

    public DyeStationBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }

    public @NotNull MapCodec<DyeStationBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyeStationBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlocks.DYE_STATION_BLOCK_ENTITY.get(), DyeStationBlockEntity::tick);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return DEFAULT_SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return DEFAULT_SHAPE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyeStationBlockEntity) {
            player.openMenu((DyeStationBlockEntity) blockEntity);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyeStationBlockEntity) {
            Containers.dropContents(world, pos, (DyeStationBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }
}
