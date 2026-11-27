package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** The client and the server both calculate how fast a player breaks a block. Both use this mixin. */
@Mixin(BlockBehaviour.class)
public abstract class EFBlockBehaviourMixin {
    @WrapOperation(
        method = "getDestroyProgress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F")
    )
    private float enchantingfixes$breakableBedrock(BlockState state, BlockGetter level, BlockPos pos, Operation<Float> original, BlockState blockState, Player player) {
        return AdvancedSilkTouch.destroySpeed(original.call(state, level, pos), state, player);
    }
}
