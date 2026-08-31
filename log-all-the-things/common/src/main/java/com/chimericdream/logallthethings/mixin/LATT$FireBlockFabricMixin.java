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
 * Fabric/vanilla-signature half of the host-block fixes split out of {@link LATT$FireBlockMixin} - see
 * that class's javadoc for why {@code checkBurnOut} and {@code isValidFireLocation} needed a platform
 * split.
 */
@Mixin(FireBlock.class)
public abstract class LATT$FireBlockFabricMixin {
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
}
