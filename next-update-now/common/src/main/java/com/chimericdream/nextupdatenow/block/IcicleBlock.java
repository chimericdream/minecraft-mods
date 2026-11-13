package com.chimericdream.nextupdatenow.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A speleothem made of ice. It uses the same growth and fall rules as pointed dripstone. It breaks
 * when a nearby light source warms it, and when it is in the Nether.
 */
public class IcicleBlock extends SpeleothemBlock {
    public static final MapCodec<IcicleBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BlockState.CODEC.fieldOf("block_to_grow_on").forGetter(block -> block.blockToGrowOn),
            propertiesCodec()
        ).apply(instance, IcicleBlock::new)
    );

    private static final int MAX_BLOCK_LIGHT = 11;
    // Level event ID of the pointed dripstone landing sound.
    private static final int LANDING_SOUND_EVENT = 1045;

    public IcicleBlock(BlockState blockToGrowOn, Properties properties) {
        super(blockToGrowOn, properties);
    }

    @Override
    public MapCodec<? extends SpeleothemBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getStalactiteLandingSound() {
        return LANDING_SOUND_EVENT;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && isTooWarm(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isTooWarm(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        super.tick(state, level, pos, random);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isTooWarm(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    private static boolean isTooWarm(Level level, BlockPos pos) {
        return level.dimension() == Level.NETHER || level.getBrightness(LightLayer.BLOCK, pos) > MAX_BLOCK_LIGHT;
    }
}
