package com.chimericdream.opus.network;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The books each connected client says it has. Books are client-side resources, so the server can only learn about them
 * from the client; this is only used to offer tab-completion in {@code /opus give}, never to decide what may be given.
 */
public final class BookCatalog {
    /** A client can't make the server hold more than this many ids per player. */
    private static final int MAX_BOOKS = 1024;

    private static final Map<UUID, List<Identifier>> BY_PLAYER = new ConcurrentHashMap<>();

    private BookCatalog() {
    }

    /** Registers the receiver and the disconnect cleanup. Called once from common init. */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            BookListPayload.ID,
            BookListPayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    List<Identifier> books = List.copyOf(payload.books().stream().limit(MAX_BOOKS).toList());
                    context.queue(() -> BY_PLAYER.put(player.getUUID(), books));
                }
            }
        );

        PlayerEvent.PLAYER_QUIT.register(player -> BY_PLAYER.remove(player.getUUID()));
    }

    /** The books {@code player}'s client reported, or an empty list if it hasn't (or has no Opus installed). */
    public static List<Identifier> booksFor(ServerPlayer player) {
        return BY_PLAYER.getOrDefault(player.getUUID(), List.of());
    }
}
