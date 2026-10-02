package com.chimericdream.allhallowssteve.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The overlay message (the line above the hotbar, same mechanism as Houdini Block's placement mode)
 * that tells a player what a decorated pumpkin holds: nothing, a torch, or one to four candles. A
 * snuffed pumpkin looks the same whether it is hollow or holds candles, so this is how a player tells
 * them apart.
 * <p>
 * Sent from three player-driven places and nowhere else: sneaking with an empty hand on the pumpkin
 * (inspect, changes nothing), and right after a player lights, snuffs, relights, adds to, or shears a
 * pumpkin. Dispenser behaviors have no player and never call {@link #show}.
 */
public final class PumpkinContents {
    public static final String EMPTY_KEY = "message.allhallowssteve.pumpkin_contents.empty";
    public static final String CANDLE_LIT_KEY = "message.allhallowssteve.pumpkin_contents.candle.lit";
    public static final String CANDLES_LIT_KEY = "message.allhallowssteve.pumpkin_contents.candles.lit";
    public static final String CANDLE_SNUFFED_KEY = "message.allhallowssteve.pumpkin_contents.candle.snuffed";
    public static final String CANDLES_SNUFFED_KEY = "message.allhallowssteve.pumpkin_contents.candles.snuffed";

    /**
     * Where {@link #show} sends the message. Always {@code Player::sendOverlayMessage} in the game; a test
     * seam, because the mock players GameTests use have no network connection and so can neither receive
     * the message nor be asked what it was.
     */
    public static volatile BiConsumer<Player, Component> messageSink = Player::sendOverlayMessage;

    private PumpkinContents() {
    }

    /** Whether {@code state} is one of the mod's decorated pumpkins. */
    public static boolean isDecoratedPumpkin(BlockState state) {
        return state.getBlock() instanceof DecoratedPumpkinBlock
            || state.getBlock() instanceof LitDecoratedPumpkinBlock
            || state.getBlock() instanceof CandleLitDecoratedPumpkinBlock;
    }

    /**
     * What {@code state} holds, or {@code null} if it isn't a decorated pumpkin. Torches use the item's
     * own name (Torch, Soul Torch, Copper Torch, Redstone Torch) and have no lit status, since they can't
     * be snuffed; only candles say lit or snuffed.
     */
    public static @Nullable Component describe(BlockState state) {
        if (state.getBlock() instanceof LitDecoratedPumpkinBlock lit) {
            return Component.translatable(lit.torchItem.getDescriptionId());
        }

        if (state.getBlock() instanceof CandleLitDecoratedPumpkinBlock) {
            int candles = state.getValue(CandleLitDecoratedPumpkinBlock.CANDLES);
            boolean lit = state.getValue(CandleLitDecoratedPumpkinBlock.LIT);

            return Component.translatable(candlesKey(candles, lit), candles);
        }

        if (state.getBlock() instanceof DecoratedPumpkinBlock) {
            return Component.translatable(EMPTY_KEY);
        }

        return null;
    }

    /** The translation key for {@code candles} candles, lit or snuffed (the singular form for one). */
    public static String candlesKey(int candles, boolean lit) {
        if (candles == 1) {
            return lit ? CANDLE_LIT_KEY : CANDLE_SNUFFED_KEY;
        }

        return lit ? CANDLES_LIT_KEY : CANDLES_SNUFFED_KEY;
    }

    /**
     * Whether {@code player} is making the inspect gesture: sneaking with an empty hand. Checked from the
     * blocks' {@code useWithoutItem}, which also runs for a sneaking player holding an item in a GameTest
     * (vanilla skips it in real play), so the empty-hand part is checked explicitly.
     */
    public static boolean isInspecting(Player player) {
        return player.isShiftKeyDown() && player.getMainHandItem().isEmpty();
    }

    /** Shows {@code player} what the pumpkin at {@code pos} currently holds. Server side only; does nothing on the client or for a non-pumpkin. */
    public static void show(Player player, Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        Component contents = describe(level.getBlockState(pos));
        if (contents != null) {
            messageSink.accept(player, contents);
        }
    }
}
