package com.chimericdream.opus.client.book;

import com.chimericdream.opus.network.BookListPayload;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Tells the server which books this client has, so {@code /opus give} can tab-complete them. Sent on joining a world and
 * after every resource reload that happens while in one.
 */
public final class BookListSync {
    private BookListSync() {
    }

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> send());
    }

    public static void send() {
        if (Minecraft.getInstance().player == null || !NetworkManager.canServerReceive(BookListPayload.ID)) {
            return;
        }

        NetworkManager.sendToServer(new BookListPayload(List.copyOf(BookRepository.all().keySet())));
    }
}
