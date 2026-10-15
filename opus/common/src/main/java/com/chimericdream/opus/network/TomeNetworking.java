package com.chimericdream.opus.network;

import com.chimericdream.opus.component.TomeContentsComponent;
import com.chimericdream.opus.item.TomeStacks;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Server side of the Book of Books controls. Both requests come from the client but the server re-checks what the
 * player is actually holding, so a stale or forged packet can't conjure anything.
 */
public final class TomeNetworking {
    private TomeNetworking() {
    }

    /** Registers both packets. Called once from common init, which registers the payload types on both sides. */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            TomeSelectPayload.ID,
            TomeSelectPayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> select(player, payload.offHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, payload.index()));
                }
            }
        );

        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            TomeRevertPayload.ID,
            TomeRevertPayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> revert(player));
                }
            }
        );
    }

    /** Turns the Book of Books in {@code hand} into the guide at {@code index}. */
    static void select(ServerPlayer player, InteractionHand hand, int index) {
        ItemStack held = player.getItemInHand(hand);
        TomeContentsComponent contents = TomeStacks.contentsOf(held);

        if (contents == null || contents.hasSelection() || index < 0 || index >= contents.books().size()) {
            return;
        }

        player.setItemInHand(hand, TomeStacks.transformed(contents, index));
    }

    /** Turns the guide in the player's main hand back into its Book of Books, if it came from one. */
    static void revert(ServerPlayer player) {
        ItemStack tome = TomeStacks.revert(player.getItemInHand(InteractionHand.MAIN_HAND));

        if (!tome.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, tome);
        }
    }
}
