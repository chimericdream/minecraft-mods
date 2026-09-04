package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.block.entity.UpsideDownChestBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class UpsideDownChestBlock extends ChestBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "upside_down_chest");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final MapCodec<UpsideDownChestBlock> CODEC = simpleCodec(UpsideDownChestBlock::new);

    // Mirror image (top<->bottom) of vanilla ChestBlock's SHAPE/HALF_SHAPES, since the model itself
    // renders flipped: 16 - maxY .. 16 - minY instead of minY .. maxY.
    private static final VoxelShape SHAPE = Block.column(14.0, 2.0, 16.0);
    private static final Map<Direction, VoxelShape> HALF_SHAPES = Shapes.rotateHorizontal(Block.boxZ(14.0, 2.0, 16.0, 0.0, 15.0));

    @Override
    public @NonNull MapCodec<UpsideDownChestBlock> codec() {
        return CODEC;
    }

    public UpsideDownChestBlock(BlockBehaviour.Properties properties) {
        super(() -> ModBlocks.UPSIDE_DOWN_CHEST_BLOCK_ENTITY.get(), SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
    }

    public static UpsideDownChestBlock create() {
        return new UpsideDownChestBlock(Properties.ofFullCopy(Blocks.CHEST).setId(BLOCK_REGISTRY_KEY));
    }

    @Override
    public @NonNull BlockEntity newBlockEntity(final @NonNull BlockPos worldPosition, final @NonNull BlockState blockState) {
        return new UpsideDownChestBlockEntity(worldPosition, blockState);
    }

    @Override
    protected @NonNull VoxelShape getShape(final @NonNull BlockState state, final @NonNull BlockGetter level, final @NonNull BlockPos pos, final @NonNull CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case SINGLE -> SHAPE;
            case LEFT, RIGHT -> HALF_SHAPES.get(getConnectedDirection(state));
        };
    }

    @Override
    public @NonNull NeighborCombineResult<? extends ChestBlockEntity> combine(
        final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final boolean ignoreBeingBlocked
    ) {
        BiPredicate<LevelAccessor, BlockPos> predicate = ignoreBeingBlocked
            ? (levelAccessor, blockPos) -> false
            : UpsideDownChestBlock::isUpsideDownChestBlockedAt;

        return DoubleBlockCombiner.combineWithNeigbour(
            this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, state, level, pos, predicate
        );
    }

    // The lid swings open into the space below an upside-down chest instead of above, so that's the
    // space that needs to be clear - mirrors vanilla's isChestBlockedAt/isBlockedChestByBlock/
    // isCatSittingOnChest, just checking pos.below() instead of pos.above().
    private static boolean isUpsideDownChestBlockedAt(final LevelAccessor level, final BlockPos pos) {
        return isBlockedByBlockBelow(level, pos) || isCatSittingBelowChest(level, pos);
    }

    private static boolean isBlockedByBlockBelow(final BlockGetter level, final BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isRedstoneConductor(level, below);
    }

    private static boolean isCatSittingBelowChest(final LevelAccessor level, final BlockPos pos) {
        List<Cat> cats = level.getEntitiesOfClass(Cat.class, new AABB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1));

        if (!cats.isEmpty()) {
            for (Cat cat : cats) {
                if (cat.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }
}
