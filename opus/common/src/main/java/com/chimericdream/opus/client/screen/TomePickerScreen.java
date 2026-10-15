package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.component.TomeContentsComponent;
import com.chimericdream.opus.item.BookOfBooksItem;
import com.chimericdream.opus.network.TomeSelectPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The menu a Book of Books opens on right-click: one button per stored guide, paged when there are many. Picking a
 * guide asks the server to turn the held Book of Books into it, then closes.
 *
 * <p>Not yet run in game; the drawing calls match the ones {@link BookMissingScreen} already uses.
 */
public class TomePickerScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 4;
    private static final int PAGE_SIZE = 7;
    private static final int PAGE_BUTTON_WIDTH = 20;

    private final InteractionHand hand;
    private final List<String> books;
    private int page;

    public TomePickerScreen(InteractionHand hand, TomeContentsComponent contents) {
        super(Component.translatable("opus.tome.title"));
        this.hand = hand;
        this.books = contents.books();
    }

    private int pageCount() {
        return (books.size() + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    @Override
    protected void init() {
        int first = page * PAGE_SIZE;
        int shown = Math.min(PAGE_SIZE, books.size() - first);
        int rows = shown + (pageCount() > 1 ? 1 : 0);
        int x = (width - BUTTON_WIDTH) / 2;
        int y = (height - (rows * (BUTTON_HEIGHT + GAP) - GAP)) / 2 + font.lineHeight;

        for (int i = first; i < first + shown; i++) {
            final int index = i;
            addRenderableWidget(Button.builder(Component.literal(BookOfBooksItem.titleOf(books.get(i))), b -> pick(index))
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            y += BUTTON_HEIGHT + GAP;
        }

        if (pageCount() > 1) {
            Button previous = addRenderableWidget(Button.builder(Component.translatable("opus.tome.prev"), b -> turn(-1))
                .bounds(x, y, PAGE_BUTTON_WIDTH, BUTTON_HEIGHT).build());
            previous.active = page > 0;

            Button next = addRenderableWidget(Button.builder(Component.translatable("opus.tome.next"), b -> turn(1))
                .bounds(x + BUTTON_WIDTH - PAGE_BUTTON_WIDTH, y, PAGE_BUTTON_WIDTH, BUTTON_HEIGHT).build());
            next.active = page < pageCount() - 1;
        }
    }

    private void turn(int by) {
        page = Math.max(0, Math.min(pageCount() - 1, page + by));
        rebuildWidgets();
    }

    private void pick(int index) {
        NetworkManager.sendToServer(new TomeSelectPayload(hand == InteractionHand.OFF_HAND, index));
        onClose();
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);

        int rows = Math.min(PAGE_SIZE, books.size()) + (pageCount() > 1 ? 1 : 0);
        int top = (height - (rows * (BUTTON_HEIGHT + GAP) - GAP)) / 2 + font.lineHeight;
        g.text(font, title, (width - font.width(title)) / 2, top - font.lineHeight - GAP * 2, 0xFFFFFFFF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
