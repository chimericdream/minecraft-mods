package com.chimericdream.logallthethings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.chimericdream.logallthethings.windowlog.WindowedBlock;
import com.chimericdream.logallthethings.windowlog.WindowedBlockEntity;

/**
 * NeoForge-patched-signature half of the host-block fixes split out of {@link LATT$FireBlockMixin} -
 * see that class's javadoc for why {@code checkBurnOut} and {@code isValidFireLocation} needed a
 * platform split.
 *
 * <p>NeoForge's patcher adds a trailing {@code Direction} parameter to {@code checkBurnOut} (for its
 * own fire-spread event hook), and rewrites {@code isValidFireLocation} - plus the per-direction
 * neighbour loop inside the 2-arg {@code getStateForPlacement} - to no longer call
 * {@code getBlockState}/{@code canBurn} directly at all, routing both through a new
 * {@code canCatchFire(BlockGetter, BlockPos, Direction)} choke point instead. Redirecting the single
 * {@code getBlockState} call inside {@code canCatchFire} covers both of those call sites at once.
 */
@Mixin(FireBlock.class)
public abstract class LATT$FireBlockNeoForgeMixin {
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
        method = "checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$checkBurnOutSeesHostBlock(Level level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }

    @Redirect(
        method = "canCatchFire(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState latt$canCatchFireSeesHostBlock(BlockGetter level, BlockPos pos) {
        return latt$effectiveFlammabilityState(level, pos, level.getBlockState(pos));
    }
}
