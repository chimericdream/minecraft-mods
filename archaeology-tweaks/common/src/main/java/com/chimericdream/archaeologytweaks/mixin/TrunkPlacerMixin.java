package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Every azalea tree (bonemealed from an Azalea/Flowering Azalea bush, or grown as the nested tree
 * inside a Lush Caves root system) plants exactly one {@link Blocks#ROOTED_DIRT} directly under its
 * trunk via this shared trunk-placer helper. See {@link RootSystemFeatureMixin} for the larger
 * source of naturally generated rooted dirt.
 */
@Mixin(TrunkPlacer.class)
public abstract class TrunkPlacerMixin {
    @Unique
    private static final float SUSPICIOUS_CHANCE = 0.1F;

    @Redirect(
        method = "placeBelowTrunkBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/stateproviders/BlockStateProvider;getOptionalState(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private static BlockState at$maybeSuspicious(BlockStateProvider provider, WorldGenLevel level, RandomSource random, BlockPos pos) {
        BlockState state = provider.getOptionalState(level, random, pos);
        if (state != null && state.is(Blocks.ROOTED_DIRT) && RandomSource.create().nextFloat() < SUSPICIOUS_CHANCE) {
            return ModBlocks.SUSPICIOUS_ROOTED_DIRT.get().defaultBlockState();
        }

        return state;
    }
}
