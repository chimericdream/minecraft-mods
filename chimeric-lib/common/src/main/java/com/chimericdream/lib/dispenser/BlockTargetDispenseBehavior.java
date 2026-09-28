package com.chimericdream.lib.dispenser;

import com.chimericdream.lib.mixin.DispenserBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * A dispenser behavior for "use this item on the block in front of the dispenser", layered on top of
 * whatever behavior the item already had. Build these with {@link DispenserBehaviors#wrap} rather
 * than directly.
 *
 * <p>Each dispense runs the {@link Handler} first. {@link Result#PASS} hands the stack to the
 * previous behavior untouched, so wrapping e.g. shears keeps vanilla's beehive/sheep shearing working
 * for every block the handler doesn't care about. {@link Result#SUCCESS} and {@link Result#FAIL} mean
 * the handler dealt with the target itself; this class then plays the same sound and smoke vanilla's
 * {@code OptionalDispenseItemBehavior} would (dispense click vs. failure click, plus the smoke puff
 * either way). Exactly one of the two paths runs per dispense, so the sound never plays twice.
 */
public final class BlockTargetDispenseBehavior implements DispenseItemBehavior {
    public enum Result {
        /** Not a block this handler cares about: run the item's previous dispenser behavior. */
        PASS,
        /** Handled: play the normal dispense sound. */
        SUCCESS,
        /** Handled, but nothing could be done: play the failure click and leave the stack as-is. */
        FAIL
    }

    @FunctionalInterface
    public interface Handler {
        /**
         * Called server-side for every dispense of a wrapped item. On {@link Result#SUCCESS}, the
         * handler is responsible for consuming/damaging {@code stack} itself (e.g.
         * {@code stack.shrink(1)} or {@link DispenserBehaviors#damageWithoutPlayer}); on
         * {@link Result#PASS} it must not touch {@code stack}.
         */
        Result handle(ServerLevel level, BlockPos target, BlockState targetState, ItemStack stack, BlockSource source);
    }

    private final Handler handler;
    private final @Nullable DispenseItemBehavior fallback;

    /**
     * @param fallback the item's previous behavior, or {@code null} to use vanilla's per-stack default
     *                 (resolved at dispense time, same as {@code DispenserBlock} itself does)
     */
    BlockTargetDispenseBehavior(Handler handler, @Nullable DispenseItemBehavior fallback) {
        this.handler = handler;
        this.fallback = fallback;
    }

    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        ServerLevel level = source.level();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos target = source.pos().relative(facing);

        Result result = handler.handle(level, target, level.getBlockState(target), stack, source);
        if (result == Result.PASS) {
            return fallbackFor(stack).dispense(source, stack);
        }

        level.levelEvent(result == Result.SUCCESS ? LevelEvent.SOUND_DISPENSER_DISPENSE : LevelEvent.SOUND_DISPENSER_FAIL, source.pos(), 0);
        level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, source.pos(), facing.get3DDataValue());

        return stack;
    }

    /** The behavior {@link Result#PASS} delegates to for {@code stack}. */
    public DispenseItemBehavior fallbackFor(ItemStack stack) {
        return fallback != null ? fallback : DispenserBlockAccessor.chimericlib$getDefaultDispenseMethod(stack);
    }
}
