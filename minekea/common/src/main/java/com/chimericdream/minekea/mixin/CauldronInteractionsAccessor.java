package com.chimericdream.minekea.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

/**
 * As of 26.1, the {@code fillBucket}/{@code emptyBucket} helpers that build a
 * {@link net.minecraft.core.cauldron.CauldronInteraction} lambda from a source/target block state moved
 * from {@code CauldronInteraction} onto package-private static methods on {@link CauldronInteractions}.
 * This accessor widens them back to public so custom cauldron blocks can keep reusing vanilla's
 * fill/empty bucket behavior instead of reimplementing it.
 */
@Mixin(CauldronInteractions.class)
public interface CauldronInteractionsAccessor {
    @Invoker("fillBucket")
    static InteractionResult minekea$invokeFillBucket(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        ItemStack stack,
        ItemStack filledStack,
        Predicate<BlockState> predicate,
        SoundEvent soundEvent
    ) {
        throw new UnsupportedOperationException("Mixin accessor not applied");
    }

    @Invoker("emptyBucket")
    static InteractionResult minekea$invokeEmptyBucket(
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        ItemStack stack,
        BlockState newState,
        SoundEvent soundEvent
    ) {
        throw new UnsupportedOperationException("Mixin accessor not applied");
    }
}
