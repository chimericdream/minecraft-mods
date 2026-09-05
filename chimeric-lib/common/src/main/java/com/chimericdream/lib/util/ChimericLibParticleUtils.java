package com.chimericdream.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ChimericLibParticleUtils {
    public static void spawnParticleAbove(final Level level, final BlockPos pos, final RandomSource random, final ParticleOptions particle) {
        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() + 1.05;
        double z = (double) pos.getZ() + random.nextDouble();
        level.addParticle(particle, x, y, z, 0.0f, 0.0f, 0.0f);
    }
}
