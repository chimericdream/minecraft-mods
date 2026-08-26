package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Vanilla's azalea root system (the Lush Caves ceiling trees) is effectively the only natural
 * source of {@link Blocks#ROOTED_DIRT} in the world. Searching for already-placed rooted dirt via
 * an ordinary ore-style feature almost never found anything to replace, so this hooks the actual
 * placement site instead: whenever the root system places real rooted dirt, there's a flat chance
 * it comes out suspicious instead. See {@link TrunkPlacerMixin} for the other (much smaller) source.
 */
@Mixin(RootSystemFeature.class)
public abstract class RootSystemFeatureMixin {
    @Unique
    private static final float SUSPICIOUS_CHANCE = 0.1F;

    @Redirect(
        method = "placeRootedDirt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        )
    )
    private static boolean at$maybeSuspicious(WorldGenLevel level, BlockPos pos, BlockState state, int flags) {
        if (state.is(Blocks.ROOTED_DIRT) && RandomSource.create().nextFloat() < SUSPICIOUS_CHANCE) {
            state = ModBlocks.SUSPICIOUS_ROOTED_DIRT.get().defaultBlockState();
        }

        return level.setBlock(pos, state, flags);
    }
}
