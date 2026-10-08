package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.client.OpusClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Shown instead of a book that could not be found: says which id was looked for and, when the default guide is
 * available, offers a button that opens its "A book says not found" troubleshooting page.
 *
 * <p>Not yet run in game; the drawing calls match the ones {@link BookScreen} already uses.
 */
public class BookMissingScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 4;

    private final Screen previous;
    private final String bookId;
    private final String guideId;
    private final String troubleshootingPage;

    /**
     * @param previous            the screen to return to when this one closes (for example the book that held the broken link), or {@code null}
     * @param guideId             the guide that holds the troubleshooting page, or {@code null} when it is not available
     * @param troubleshootingPage the page to open in {@code guideId}
     */
    public BookMissingScreen(Screen previous, String bookId, String guideId, String troubleshootingPage) {
        super(Component.translatable("opus.book.missing", bookId));
        this.previous = previous;
        this.bookId = bookId;
        this.guideId = guideId;
        this.troubleshootingPage = troubleshootingPage;
    }

    @Override
    protected void init() {
        int x = (width - BUTTON_WIDTH) / 2;
        int y = height / 2 + font.lineHeight;

        if (guideId != null) {
            addRenderableWidget(Button.builder(Component.translatable("opus.book.missing.help"),
                    b -> OpusClient.openBook(guideId, troubleshootingPage))
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            y += BUTTON_HEIGHT + GAP;
        }

        addRenderableWidget(Button.builder(Component.translatable("opus.book.missing.close"), b -> onClose())
            .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);

        Component message = Component.translatable("opus.book.missing", bookId);
        g.text(font, message, (width - font.width(message)) / 2, height / 2 - font.lineHeight * 2, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(previous);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
