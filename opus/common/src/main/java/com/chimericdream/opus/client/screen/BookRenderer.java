package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.Element;
import com.chimericdream.opus.core.layout.Layout;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Draws a {@link Layout} into a scrolled viewport. Everything here is a direct translation of the layout
 * engine's elements; no layout decisions are made at draw time.
 *
 * <p>Compiles against 26.2; not yet run in game. Drawing calls ({@code fill}, {@code text}, {@code item}, {@code pose()}
 * matrix ops, scissor) follow the 26.2 {@code GuiGraphicsExtractor} usage seen in minekea, all-hallows-steve
 * and better-target-dummies, but several signatures are best guesses - see the notes inline.
 */
final class BookRenderer {
    private final Font font;
    private final BookTheme theme;
    private final BookWidgets widgets;

    BookRenderer(Font font, BookTheme theme) {
        this.font = font;
        this.theme = theme;
        this.widgets = new BookWidgets(font, theme);
    }

    /**
     * @param originX screen x of the layout's x=0
     * @param originY screen y of the layout's y=0 *before* scrolling (i.e. the viewport top)
     * @param scroll  how many pixels the content is scrolled up
     * @param mouseX  screen coordinates, used for link hover and widget tooltips
     */
    void draw(GuiGraphicsExtractor g, Layout layout, int originX, int originY, int scroll, int viewHeight, int mouseX, int mouseY) {
        int top = scroll - 24;
        int bottom = scroll + viewHeight + 24;
        int localMouseX = mouseX - originX;
        int localMouseY = mouseY - originY + scroll;

        for (Element element : layout.elements()) {
            if (element.y() + element.height() < top || element.y() > bottom) {
                continue;
            }

            int x = originX + element.x();
            int y = originY + element.y() - scroll;

            switch (element) {
                case Element.Rect r -> drawRect(g, r, x, y);
                case Element.Text t -> drawText(g, t, x, y, element.contains(localMouseX, localMouseY));
                case Element.Icon icon -> drawIcon(g, icon, x, y);
                case Element.WidgetBox box -> widgets.draw(g, box, x, y, mouseX, mouseY);
            }
        }
    }

    private void drawRect(GuiGraphicsExtractor g, Element.Rect r, int x, int y) {
        int color = switch (r.role()) {
            case CODE_BACKGROUND -> theme.codeBackground();
            case QUOTE_BAR -> theme.quoteBar();
            case RULE, HEADING_UNDERLINE -> theme.rule();
            case TABLE_BORDER -> theme.tableBorder();
            case TABLE_HEADER -> theme.tableHeader();
            case CALLOUT_BACKGROUND -> (theme.calloutColor(r.variant()) & 0x00FFFFFF) | 0x22000000;
            case CALLOUT_BAR -> theme.calloutColor(r.variant());
        };

        g.fill(x, y, x + r.width(), y + r.height(), color);
    }

    private void drawText(GuiGraphicsExtractor g, Element.Text t, int x, int y, boolean hovered) {
        int color = t.role() == Element.TextRole.LINK && hovered ? theme.linkHover() : theme.textColor(t.role());
        MutableComponent text = MinecraftTextMetrics.component(t.text(), t.style(), t.link() != null);

        if (t.scale() == 1f) {
            // The 6-argument overload with dropShadow=false is a guess; parchment text looks smudged with a shadow.
            g.text(font, text, x, y, color, false);
        } else {
            g.pose().pushMatrix();
            g.pose().translate(x, y);
            g.pose().scale(t.scale(), t.scale());
            g.text(font, text, 0, 0, color, false);
            g.pose().popMatrix();
        }
    }

    private void drawIcon(GuiGraphicsExtractor g, Element.Icon icon, int x, int y) {
        // Blocks and items share ids for nearly everything; entities have no item form, so they show a spawn
        // egg-less placeholder for now (TODO: render the entity or its spawn egg).
        if (icon.kind().equals("entity")) {
            g.text(font, Component.literal("?"), x + 5, y + 4, theme.muted(), false);
            return;
        }

        g.item(ItemLookup.stack(icon.id(), 1), x, y);
    }
}
