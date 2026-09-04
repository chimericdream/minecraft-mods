package com.chimericdream.logallthethings.lavalog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Queries whether a block is registered as flammable, so lava-logging can refuse flammable variants
 * (e.g. oak stairs) while still allowing non-flammable ones of the same block class (e.g. stone
 * stairs). Wired via a platform-registered {@link Provider} rather than {@code @ExpectPlatform}: that
 * annotation requires its generated {@code Impl} class to live in this same package on the platform
 * source set, but NeoForge's dev run resolves common and the neoforge source set as separate JPMS
 * modules, so two modules end up exporting this package and FML fails to start. See
 * {@code CampfireGraceHolder} in sneaky-tweaks for the prior incident this avoids.
 *
 * <p>Takes the same {@code (level, pos, state)} vanilla passes into {@code canPlaceLiquid} rather than
 * just a {@link net.minecraft.world.level.block.Block}: NeoForge's native query
 * ({@code BlockState#isFlammable}) is state/context-based, while Fabric's
 * {@code FlammableBlockRegistry} only ever keys off the block itself and ignores the rest.
 */
public final class LavaLogFlammability {
    private static Provider provider;

    private LavaLogFlammability() {
    }

    public static void setProvider(Provider platformProvider) {
        provider = platformProvider;
    }

    public static boolean isFlammable(BlockGetter level, BlockPos pos, BlockState state) {
        return provider.isFlammable(level, pos, state);
    }

    public interface Provider {
        boolean isFlammable(BlockGetter level, BlockPos pos, BlockState state);
    }
}
