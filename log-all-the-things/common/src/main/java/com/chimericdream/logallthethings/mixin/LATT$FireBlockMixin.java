package com.chimericdream.logallthethings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.chimericdream.logallthethings.windowlog.WindowedBlock;
import com.chimericdream.logallthethings.windowlog.WindowedBlockEntity;

/**
 * {@code FireBlock} decides ignite/burn odds purely from {@code state.getBlock()} looked up in its own
 * {@code Object2IntMap<Block>} tables (see {@code FireBlock#bootStrap}), keyed by the vanilla block
 * class - a window-logged host is always the single, blockstate-less {@link WindowedBlock} instance, so
 * without help fire treats it as entirely non-flammable regardless of what's actually embedded inside
 * (an oak stairs host should burn like oak; a stone brick stairs host shouldn't burn at all).
 *
 * <p>Every decision point below independently re-fetches {@code BlockState} via {@code getBlockState},
 * discarding position by the time it reaches the actual odds tables - the fix has to happen at each of
 * these {@code getBlockState} call sites instead, substituting the block entity's real
 * {@code hostState} whenever the neighbour being inspected is a {@code WindowedBlock}. This covers the
 * two mechanics that actually make a block "flammable" in vanilla: spreading fire into a new adjacent
 * air pocket ({@code getIgniteOdds(LevelReader, BlockPos)}), and an already-burning neighbour
 * eventually consuming the block ({@code checkBurnOut}) - plus the two checks that decide whether fire
 * can exist/keep existing next to it at all ({@code isValidFireLocation}, and the flint-and-steel
 * placement check in the 2-arg {@code getStateForPlacement}).
 */
@Mixin(FireBlock.class)
public abstract class LATT$FireBlockMixin {
    @Unique
    private static BlockState latt$effectiveFlammabilityState(BlockGetter level, BlockPos pos, BlockState realState) {
        if (realState.getBlock() instanceof WindowedBlock && level.getBlockEntity(pos) instanceof WindowedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                return hostState;
            }
        }

        return realState;
    }

    @Redirect(
        method = "getIgniteOdds(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$igniteOddsSeesHostBlock(LevelReader level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }

    @Redirect(
        method = "checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$checkBurnOutSeesHostBlock(Level level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }

    @Redirect(
        method = "isValidFireLocation",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$isValidFireLocationSeesHostBlock(BlockGetter level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }

    @Redirect(
        method = "getStateForPlacement(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$getStateForPlacementSeesHostBlock(BlockGetter level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }
}
