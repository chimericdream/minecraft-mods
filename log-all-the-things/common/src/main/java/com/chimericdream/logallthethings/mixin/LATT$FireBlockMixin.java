package com.chimericdream.logallthethings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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
 * {@code hostState} whenever the neighbour being inspected is a {@code WindowedBlock}. This covers
 * two of the mechanics that actually make a block "flammable" in vanilla: spreading fire into a new
 * adjacent air pocket ({@code getIgniteOdds(LevelReader, BlockPos)}), and the below-block sturdiness
 * check in the flint-and-steel placement check ({@code getStateForPlacement}).
 *
 * <p>Two more mechanics - an already-burning neighbour eventually consuming the block
 * ({@code checkBurnOut}), and whether fire can exist/keep existing next to a block at all
 * ({@code isValidFireLocation}, plus {@code getStateForPlacement}'s own per-direction neighbour loop)
 * - are handled by the platform-specific {@code LATT$FireBlockFabricMixin} /
 * {@code LATT$FireBlockNeoForgeMixin} instead. NeoForge's patcher restructures {@code FireBlock} so
 * these no longer call {@code getBlockState} directly: {@code checkBurnOut} gains a trailing
 * {@code Direction} parameter, and {@code isValidFireLocation}/the neighbour loop route through a new
 * {@code canCatchFire(BlockGetter, BlockPos, Direction)} choke point instead of inlining
 * {@code getBlockState} + {@code canBurn} - so no single {@code method =}/{@code @At} pair matches both
 * platforms for those two mechanics.
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
