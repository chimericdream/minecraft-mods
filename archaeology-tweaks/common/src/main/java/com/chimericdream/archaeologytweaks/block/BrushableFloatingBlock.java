package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrushableFloatingBlock extends BrushableBlock {
    public BrushableFloatingBlock(Block baseBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, BlockBehaviour.Properties settings) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ATBrushableBlockEntity(pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity var6 = world.getBlockEntity(pos);
        if (var6 instanceof ATBrushableBlockEntity brushableBlockEntity) {
            brushableBlockEntity.scheduledTick(world);
        }
    }

    @Override
    public void onBrokenAfterFall(Level world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
    }
}
