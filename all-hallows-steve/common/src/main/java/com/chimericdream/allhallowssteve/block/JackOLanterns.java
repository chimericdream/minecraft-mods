package com.chimericdream.allhallowssteve.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * The vanilla-pumpkin counterpart to {@link DecoratedPumpkinBlock#light} /
 * {@link LitDecoratedPumpkinBlock#extinguish}: a regular torch turns a carved pumpkin into a
 * jack o'lantern, and shears turn a jack o'lantern back into a carved pumpkin, dropping the torch.
 * Vanilla only pairs the jack o'lantern with a regular torch, so the other three torch types don't
 * light a carved pumpkin.
 * <p>
 * Driven by the player from {@code AHS$TorchBlockItemMixin} / {@code AHS$ShearsItemMixin}, and by
 * dispensers from {@link ModDispenserBehaviors}. Both swaps go through {@code setBlock} with the
 * normal update flags, same as vanilla's own shears-carving in {@code PumpkinBlock}, so
 * {@code CarvedPumpkinBlock#onPlace} still runs its golem check exactly as it would for a freshly
 * carved pumpkin.
 */
public final class JackOLanterns {
    private JackOLanterns() {
    }

    public static boolean canLight(BlockState state, ItemStack stack) {
        return state.is(Blocks.CARVED_PUMPKIN) && stack.is(Items.TORCH);
    }

    public static boolean isLit(BlockState state) {
        return state.is(Blocks.JACK_O_LANTERN);
    }

    /** Swaps the carved pumpkin at {@code pos} for a jack o'lantern with the same facing; consuming the torch is left to the caller. */
    public static void light(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        level.setBlock(pos, Blocks.JACK_O_LANTERN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, state.getValue(CarvedPumpkinBlock.FACING)), Block.UPDATE_ALL);

        level.gameEvent(cause, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /** Swaps the jack o'lantern at {@code pos} for a carved pumpkin with the same facing and drops a torch; damaging the shears is left to the caller. */
    public static void extinguish(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, state.getValue(CarvedPumpkinBlock.FACING)), Block.UPDATE_ALL);
        Block.popResource(level, pos, new ItemStack(Items.TORCH));

        level.gameEvent(cause, GameEvent.SHEAR, pos);
        level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
