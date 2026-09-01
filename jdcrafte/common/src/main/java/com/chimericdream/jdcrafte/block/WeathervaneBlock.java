package com.chimericdream.jdcrafte.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A decorative rooftop weathercock. Its only blockstate property is {@link #ROTATION} (0-7, 45 degrees
 * per step, same 8-way convention as an item frame's held-item rotation), which right-clicking cycles
 * through one step at a time - see {@link #useWithoutItem}.
 *
 * <p>Vanilla's blockstate "variant" model rotation only supports 90 degree increments, and several of
 * this model's elements (the rotating arrow assembly) already carry their own single-axis 45 degree
 * rotation authored in {@code models/block/weathervane.json} - composing an additional whole-model
 * 45k-degree Y rotation on top isn't expressible as a single per-element rotation. So every
 * {@code ROTATION} value shares the exact same static model (see
 * {@code WeathervaneBlockDataGenerator}), {@link #getRenderShape} returns {@link RenderShape#INVISIBLE}
 * to suppress vanilla's own per-chunk model placement, and {@code WeathervaneBlockEntityRenderer}
 * renders that same model manually every frame with the Y rotation applied as a runtime pose transform.
 */
public class WeathervaneBlock extends BaseEntityBlock {
    public static final MapCodec<WeathervaneBlock> CODEC = simpleCodec(WeathervaneBlock::new);

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);

    // Approximate collision for the post + rotating arrow assembly. The assembly's real footprint spins
    // with ROTATION, but a single fixed box is a reasonable simplification for a thin decorative
    // fixture that a player is never meant to stand on.
    private static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 13.0, 10.0);

    public WeathervaneBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    protected MapCodec<WeathervaneBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WeathervaneBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int rotation = state.getValue(ROTATION);
        int newRotation = rotation <= 0 ? 7 : rotation - 1;
        if (player.isShiftKeyDown()) {
            newRotation = rotation >= 7 ? 0 : rotation + 1;
        }

        level.setBlockAndUpdate(pos, state.setValue(ROTATION, newRotation));
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }
}
