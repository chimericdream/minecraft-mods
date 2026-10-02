package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.PumpkinContents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Records the contents messages {@link PumpkinContents} would send, in place of the network send a mock
 * player can't do. GameTests run interleaved in the same tick, so messages are kept per player: call
 * {@link #install()} when making a mock player, and read that player's messages with {@link #keys}.
 * The sink stays installed for the rest of the test run, which is fine for a test-only mod.
 */
final class PumpkinMessageRecorder {
    private static final Map<Player, List<Component>> MESSAGES = new IdentityHashMap<>();

    private PumpkinMessageRecorder() {
    }

    static void install() {
        PumpkinContents.messageSink = (player, message) -> MESSAGES.computeIfAbsent(player, key -> new ArrayList<>()).add(message);
    }

    /** The translation key of each message sent to {@code player}, oldest first. */
    static List<String> keys(Player player) {
        return MESSAGES.getOrDefault(player, List.of()).stream()
            .map(message -> message.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : message.getString())
            .toList();
    }
}
