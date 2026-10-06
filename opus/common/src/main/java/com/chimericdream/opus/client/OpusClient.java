package com.chimericdream.opus.client;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.client.book.BookRepository;
import com.chimericdream.opus.client.book.OpusReloadListener;
import com.chimericdream.opus.client.screen.BookScreen;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.item.OpusBookItem;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Client entry point shared by both loaders (called from the Fabric client initializer and NeoForge's
 * {@code FMLClientSetupEvent}).
 *
 * <p>Compiles against 26.2; not yet run in game. {@code ClientTickEvent.CLIENT_POST} is Architectury's loader-neutral tick hook
 * (the other mods register their tick handlers per loader instead).
 */
public final class OpusClient {
    private OpusClient() {
    }

    public static void init() {
        Keybindings.init();
        OpusReloadListener.register();

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (Keybindings.OPEN_GUIDE.consumeClick()) {
                openBook(OpusBookItem.DEFAULT_BOOK);
            }
        });
    }

    /** Opens {@code bookId} at its home page, or tells the player the book is missing. */
    public static void openBook(String bookId) {
        openBook(bookId, null);
    }

    /** @param pageId node id to open on, or {@code null} for the book's home page */
    public static void openBook(String bookId, String pageId) {
        Minecraft minecraft = Minecraft.getInstance();
        Book book = BookRepository.get(bookId);

        if (book == null) {
            if (minecraft.player != null) {
                minecraft.player.sendOverlayMessage(Component.translatable("opus.book.missing", bookId));
            }
            OpusMod.LOGGER.warn("Tried to open unknown book '{}'", bookId);
            return;
        }

        minecraft.setScreenAndShow(new BookScreen(book, pageId));
    }
}
