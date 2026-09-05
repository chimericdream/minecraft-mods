package com.chimericdream.nextupdatenow.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

public class ModLeavesBlock extends LeavesBlock {
    public static final MapCodec<ModLeavesBlock> CODEC = simpleCodec(properties -> new ModLeavesBlock(0.1F, properties));

    public ModLeavesBlock(float leafParticleChance, Properties properties) {
        super(leafParticleChance, properties);
    }

    @Override
    public MapCodec<ModLeavesBlock> codec() {
        return CODEC;
    }

    /**
     * Vanilla's per-species falling-leaf particle (see the copied
     * assets/minecraft/particles/orange_poplar_leaves.json) needs its own registered ParticleType,
     * which chimeric-lib's ModRegistryHelper has no DeferredRegister for yet. Leave it a no-op rather
     * than spawning a mismatched vanilla leaf particle (e.g. cherry) in poplar's color.
     */
    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
    }
}
