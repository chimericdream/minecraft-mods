package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.client.OpusClient;
import com.mojang.blaze3d.platform.InputConstants;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.core.book.BookNode;
import com.chimericdream.opus.core.book.IconRef;
import com.chimericdream.opus.core.book.LinkTarget;
import com.chimericdream.opus.core.book.SearchIndex;
import com.chimericdream.opus.core.layout.Element;
import com.chimericdream.opus.core.layout.Layout;
import com.chimericdream.opus.core.layout.LayoutConfig;
import com.chimericdream.opus.core.layout.LayoutEngine;
import com.chimericdream.opus.core.layout.WidgetSizer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The reading screen: a table of contents sidebar (with search) on the left and one scrolling page on the
 * right, with back-history and previous/next buttons. All text flow comes from {@link LayoutEngine}; this class
 * only owns geometry, input and navigation.
 *
 * <p>Compiles against 26.2; not yet run in game. Input handlers use the {@code MouseButtonEvent}/{@code KeyEvent} signatures
 * seen in better-target-dummies' {@code MobPickerScreen}; {@code mouseScrolled}, {@code Button.builder},
 * {@code EditBox} and {@code ConfirmLinkScreen.confirmLinkNow} are standard but unchecked against 26.2. Drawing is
 * done in {@code extractBackground} (a hook the other mods' screens already override) so the widgets the base
 * class draws afterwards sit on top of the book.
 */
public class BookScreen extends Screen {
    private static final int MARGIN = 8;
    /** The sidebar is this wide on a roomy screen and shrinks to a quarter of the panel on a small one. */
    private static final int SIDEBAR_MAX_WIDTH = 136;
    private static final int SIDEBAR_MIN_WIDTH = 90;
    private static final int PAD = 8;
    private static final int HEADER_HEIGHT = 22;
    private static final int FOOTER_HEIGHT = 24;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int TOC_ROW = 18;
    private static final int ARROW_SIZE = 9;
    private static final int ARROW_GUTTER = 11;
    private static final int INDENT = 6;
    private static final int MAX_INDENT_DEPTH = 8;

    /** Long lines are hard to read, so the text column never gets wider than about this many characters. */
    private static final int MAX_COLUMN_CHARS = 100;
    private static final String CRUMB_SEPARATOR = "  >  ";
    private static final String CRUMB_ELLIPSIS = "...";

    private record TocEntry(BookNode node, int depth) {
    }

    /** A clickable breadcrumb as last drawn, in screen coordinates. */
    private record Crumb(BookNode node, int x, int width) {
    }

    private final Book book;
    private final BookTheme theme = BookTheme.PARCHMENT;

    private BookNode current;
    private String pendingAnchor;
    private Layout layout;
    private int scroll;
    private int tocScroll;
    private final Deque<BookNode> backStack = new ArrayDeque<>();

    /** Which chapters are open in the sidebar, per book id. Chapters start closed and stay as the player left them. */
    private static final Map<String, Set<String>> EXPANDED = new HashMap<>();

    private final Set<String> expanded;

    /** Every listed page in reading order, whether or not its chapter is open; previous/next follow this. */
    private List<BookNode> reading = List.of();
    /** The rows the sidebar currently shows: closed chapters hide their contents. */
    private List<TocEntry> toc = List.of();
    private List<SearchIndex.Hit> hits = List.of();

    private BookRenderer renderer;
    private final MobPreviews mobs = new MobPreviews();
    private EditBox search;
    private Button clearSearch;
    private Button previous;
    private Button next;
    private Button backButton;
    private List<Crumb> crumbs = List.of();

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;
    private int sidebarW;
    private int contentX;
    private int contentW;
    private int columnX;
    private int columnW;
    private int viewTop;
    private int viewHeight;

    public BookScreen(Book book, String pageId) {
        super(Component.literal(book.root().title()));
        this.book = book;

        BookNode start = book.root();
        if (pageId != null && !pageId.isBlank()) {
            if (book.resolve(book.root(), "/" + pageId) instanceof LinkTarget.Page page) {
                start = page.node();
            }
        }
        this.current = start;
        this.expanded = EXPANDED.computeIfAbsent(book.id(), id -> new HashSet<>());
        reveal(start);
    }

    @Override
    protected void init() {
        panelX = MARGIN;
        panelY = MARGIN;
        panelW = width - 2 * MARGIN;
        panelH = height - 2 * MARGIN;
        sidebarW = Math.max(SIDEBAR_MIN_WIDTH, Math.min(SIDEBAR_MAX_WIDTH, panelW / 4));
        contentX = panelX + sidebarW + PAD;
        contentW = panelW - sidebarW - 2 * PAD;
        viewTop = panelY + HEADER_HEIGHT;
        viewHeight = panelH - HEADER_HEIGHT - FOOTER_HEIGHT;

        // The text column is capped for readability and centred in the space beside the sidebar.
        int available = contentW - SCROLLBAR_WIDTH - 2;
        columnW = Math.min(available, MAX_COLUMN_CHARS * font.width("0"));
        columnX = contentX + (available - columnW) / 2;

        renderer = new BookRenderer(font, theme, mobs);
        reading = buildReading();
        toc = buildToc();
        scrollTocToCurrent();

        int searchWidth = sidebarW - 8 - 16;
        search = addRenderableWidget(new EditBox(font, panelX + 4, panelY + 4, searchWidth, 14, Component.empty()));
        search.setHint(Component.translatable("opus.book.search").withStyle(EditBox.SEARCH_HINT_STYLE));
        search.setResponder(query -> {
            hits = book.search(query);
            tocScroll = 0;
            if (clearSearch != null) {
                clearSearch.active = !query.isEmpty();
            }
        });

        clearSearch = addRenderableWidget(Button.builder(Component.literal("x"), b -> clearSearch())
            .bounds(panelX + 4 + searchWidth + 2, panelY + 4, 14, 14).build());
        clearSearch.active = false;

        int footerY = panelY + panelH - FOOTER_HEIGHT + 2;
        int backWidth = 50;
        previous = addRenderableWidget(Button.builder(Component.literal("<"), b -> step(-1))
            .bounds(columnX, footerY, 20, 18).build());
        backButton = addRenderableWidget(Button.builder(Component.translatable("opus.book.back"), b -> back())
            .bounds(columnX + (columnW - backWidth) / 2, footerY, backWidth, 18).build());
        next = addRenderableWidget(Button.builder(Component.literal(">"), b -> step(1))
            .bounds(columnX + columnW - 20, footerY, 20, 18).build());

        relayout();
    }

    /** Empties the search box and returns focus to it, like the clear button in other mods' search fields. */
    private void clearSearch() {
        search.setValue("");
        setFocused(search);
    }

    // ---- navigation ----

    private void navigate(BookNode node, String anchor, boolean remember) {
        if (remember && node != current) {
            backStack.push(current);
        }

        current = node;
        pendingAnchor = anchor;
        scroll = 0;

        // Make sure the page being read is visible in the sidebar, whichever way we got here.
        reveal(node);
        toc = buildToc();
        scrollTocToCurrent();
        relayout();
    }

    private void back() {
        if (!backStack.isEmpty()) {
            navigate(backStack.pop(), null, false);
        }
    }

    /** Previous/next page in reading order, skipping hidden pages. */
    private void step(int delta) {
        int index = reading.indexOf(current) + delta;
        if (index >= 0 && index < reading.size()) {
            navigate(reading.get(index), null, true);
        }
    }

    private void follow(String destination) {
        switch (book.resolve(current, destination)) {
            case LinkTarget.Page page -> navigate(page.node(), page.anchor(), true);
            case LinkTarget.External external -> ConfirmLinkScreen.confirmLinkNow(this, external.url());
            case LinkTarget.OtherBook other -> OpusClient.openBook(other.bookId(), other.pagePath());
            case LinkTarget.Reference ignored -> {
                // Item/entity references only show a tooltip; nothing to open yet.
            }
            case LinkTarget.Broken ignored -> {
            }
        }
    }

    private void relayout() {
        if (renderer == null) {
            return;
        }

        LayoutEngine engine = new LayoutEngine(new MinecraftTextMetrics(font), LayoutConfig.defaults(), WidgetSizer.withMobs(mobs::bounds, () -> minecraft.getWindow().getGuiScale()));
        layout = engine.layoutPage(current.title(), current.document(), columnW);

        if (pendingAnchor != null) {
            scroll = layout.anchorY(pendingAnchor).orElse(0);
            pendingAnchor = null;
        }
        clampScroll();

        if (previous != null) {
            int index = reading.indexOf(current);
            previous.active = index > 0;
            next.active = index >= 0 && index < reading.size() - 1;
            backButton.active = !backStack.isEmpty();
        }
    }

    private void clampScroll() {
        scroll = Math.max(0, Math.min(scroll, Math.max(0, layout.height() - viewHeight)));
    }

    // ---- table of contents ----

    /** Hidden and still-locked pages are not listed anywhere in the sidebar. */
    private boolean listed(BookNode node) {
        return !node.hidden() && node.isUnlocked(this::advancementDone);
    }

    /** A chapter the sidebar can open or close: it has at least one listed page or sub-chapter. The book itself never closes. */
    private boolean expandable(BookNode node) {
        return node != book.root() && node.children().stream().anyMatch(this::listed);
    }

    private List<BookNode> buildReading() {
        List<BookNode> out = new ArrayList<>();
        collectReading(book.root(), out);
        return out;
    }

    private void collectReading(BookNode node, List<BookNode> out) {
        if (!listed(node)) {
            return;
        }

        out.add(node);
        node.children().forEach(child -> collectReading(child, out));
    }

    /** The sidebar rows: the whole tree, except that the contents of closed chapters are skipped. */
    private List<TocEntry> buildToc() {
        List<TocEntry> out = new ArrayList<>();
        collect(book.root(), 0, out);
        return out;
    }

    private void collect(BookNode node, int depth, List<TocEntry> out) {
        if (!listed(node)) {
            return;
        }

        out.add(new TocEntry(node, depth));
        if (node == book.root() || expanded.contains(node.id())) {
            node.children().forEach(child -> collect(child, depth + 1, out));
        }
    }

    /** Opens every chapter above {@code node}, and {@code node} itself when it is a chapter with contents. */
    private void reveal(BookNode node) {
        for (BookNode ancestor = node.parent(); ancestor != null; ancestor = ancestor.parent()) {
            if (ancestor != book.root()) {
                expanded.add(ancestor.id());
            }
        }
        if (expandable(node)) {
            expanded.add(node.id());
        }
    }

    private void toggle(BookNode chapter) {
        if (!expanded.remove(chapter.id())) {
            expanded.add(chapter.id());
        }

        toc = buildToc();
        int rows = toc.size() * TOC_ROW;
        tocScroll = Math.max(0, Math.min(tocScroll, Math.max(0, rows - (panelH - 26))));
    }

    /** Scrolls the sidebar the least amount needed to bring the current page's row into view. */
    private void scrollTocToCurrent() {
        int viewport = panelH - 26;
        for (int i = 0; i < toc.size(); i++) {
            if (toc.get(i).node() == current) {
                int top = i * TOC_ROW;
                if (top < tocScroll) {
                    tocScroll = top;
                } else if (top + TOC_ROW > tocScroll + viewport) {
                    tocScroll = top + TOC_ROW - viewport;
                }
                return;
            }
        }
    }

    /**
     * Whether the player has completed an advancement, for {@code requires:} gating.
     *
     * <p>TODO: ask the client's advancement tracker, roughly
     * {@code minecraft.getConnection().getAdvancements()} -> look up the {@code AdvancementHolder} by id ->
     * {@code AdvancementProgress#isDone()}. Until that is wired up, nothing is locked.
     */
    private boolean advancementDone(String advancementId) {
        return true;
    }

    // ---- drawing ----

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);

        g.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, theme.border());
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, theme.page());
        g.fill(panelX, panelY, panelX + sidebarW, panelY + panelH, theme.sidebar());

        drawSidebar(g, mouseX, mouseY);
        drawHeader(g, mouseX, mouseY);
        drawContent(g, mouseX, mouseY);
    }

    private void drawSidebar(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int top = panelY + 22;
        int bottom = panelY + panelH - 4;
        g.enableScissor(panelX, top, panelX + sidebarW, bottom);

        boolean searching = !search.getValue().isBlank();
        int count = searching ? hits.size() : toc.size();
        BookNode holder = visibleHolderOfCurrent();
        for (int i = 0; i < count; i++) {
            int y = top + i * TOC_ROW - tocScroll;
            if (y + TOC_ROW < top || y > bottom) {
                continue;
            }

            BookNode node = searching ? hits.get(i).node() : toc.get(i).node();
            int depth = searching ? 0 : toc.get(i).depth();
            boolean selected = node == current;
            boolean holdsCurrent = !searching && !selected && node == holder;
            boolean hovered = mouseX >= panelX && mouseX < panelX + sidebarW && mouseY >= y && mouseY < y + TOC_ROW && mouseY >= top && mouseY < bottom;

            if (selected || holdsCurrent || hovered) {
                // A closed chapter that contains the page being read gets a softer version of the selection colour.
                int fill = selected ? theme.sidebarSelected()
                    : holdsCurrent ? (theme.sidebarSelected() & 0x00FFFFFF) | 0x80000000
                    : 0x22000000;
                g.fill(panelX, y, panelX + sidebarW, y + TOC_ROW, fill);
            }

            int x = panelX + 4;
            if (!searching && depth > 0) {
                // Every row below the book itself gets an arrow gutter so titles line up; only chapters draw an arrow.
                int ax = arrowX(depth);
                if (expandable(node)) {
                    drawArrow(g, ax, y + (TOC_ROW - ARROW_SIZE) / 2, expanded.contains(node.id()));
                }
                x = ax + ARROW_GUTTER;
            }

            IconRef icon = node.icon();
            if (icon != null && icon.kind() == IconRef.Kind.ITEM) {
                g.item(ItemLookup.stack(icon.id(), 1), x, y + 1);
                x += 18;
            }
            // TODO(icons): IconRef.Kind.TEXTURE - draw the texture with blit once a loader for it exists.

            int room = panelX + sidebarW - x - 4;
            String title = font.plainSubstrByWidth(node.title(), room);
            g.text(font, Component.literal(title), x, y + (TOC_ROW - font.lineHeight) / 2, theme.ink(), false);

            // A title that did not fit shows in full while the row is hovered.
            if (hovered && !title.equals(node.title())) {
                g.setTooltipForNextFrame(font, Component.literal(node.title()), mouseX, mouseY);
            }
        }

        g.disableScissor();
    }

    /** The page being read if it has a row in the sidebar, otherwise the nearest chapter above it that does. */
    private BookNode visibleHolderOfCurrent() {
        for (BookNode node = current; node != null; node = node.parent()) {
            for (TocEntry entry : toc) {
                if (entry.node() == node) {
                    return node;
                }
            }
        }

        return null;
    }

    /** x of the arrow gutter for a row at {@code depth} (1 = a top-level chapter or page); deep nesting stops indenting. */
    private int arrowX(int depth) {
        return panelX + 4 + (Math.min(depth, MAX_INDENT_DEPTH) - 1) * INDENT;
    }

    /** A small solid triangle: pointing down when the chapter is open, right when it is closed. */
    private void drawArrow(GuiGraphicsExtractor g, int x, int y, boolean open) {
        for (int i = 0; i < 5; i++) {
            if (open) {
                g.fill(x + i, y + 2 + i, x + ARROW_SIZE - i, y + 3 + i, theme.ink());
            } else {
                g.fill(x + 2 + i, y + i, x + 3 + i, y + ARROW_SIZE - i, theme.ink());
            }
        }
    }

    /**
     * Draws the breadcrumb trail in the muted text colour (not link blue); every crumb except the current page
     * can be clicked, and shows an underline while hovered. When the trail is too wide the front is shortened
     * to "...".
     */
    private void drawHeader(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        List<BookNode> chain = book.breadcrumb(current);
        int separatorWidth = font.width(CRUMB_SEPARATOR);
        int reserve = font.width(CRUMB_ELLIPSIS) + separatorWidth;

        // Keep as many trailing crumbs as fit.
        int start = chain.size();
        int used = 0;
        while (start > 0) {
            int width = font.width(chain.get(start - 1).title());
            int add = used == 0 ? width : width + separatorWidth;
            int needed = used + add + (start - 1 > 0 ? reserve : 0);
            if (used > 0 && needed > columnW) {
                break;
            }
            used += add;
            start--;
        }

        int textY = panelY + (HEADER_HEIGHT - font.lineHeight) / 2;
        boolean inBand = mouseY >= panelY && mouseY < panelY + HEADER_HEIGHT;
        int x = columnX;
        List<Crumb> drawn = new ArrayList<>();

        if (start > 0) {
            g.text(font, Component.literal(CRUMB_ELLIPSIS + CRUMB_SEPARATOR), x, textY, theme.muted(), false);
            x += reserve;
        }

        for (int i = start; i < chain.size(); i++) {
            BookNode node = chain.get(i);
            boolean last = i == chain.size() - 1;
            String title = last ? font.plainSubstrByWidth(node.title(), Math.max(1, columnX + columnW - x)) : node.title();
            int width = font.width(title);

            boolean clickable = !last;
            boolean hovered = clickable && inBand && mouseX >= x && mouseX < x + width;
            g.text(font, MinecraftTextMetrics.component(title, com.chimericdream.opus.core.model.Style.PLAIN, hovered), x, textY, theme.muted(), false);
            if (clickable) {
                drawn.add(new Crumb(node, x, width));
            }

            x += width;
            if (!last) {
                g.text(font, Component.literal(CRUMB_SEPARATOR), x, textY, theme.muted(), false);
                x += separatorWidth;
            }
        }

        crumbs = drawn;
    }

    private void drawContent(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.enableScissor(contentX, viewTop, contentX + contentW, viewTop + viewHeight);
        renderer.draw(g, layout, columnX, viewTop, scroll, viewHeight, mouseX, mouseY);
        g.disableScissor();

        if (layout.height() > viewHeight) {
            int trackX = contentX + contentW - SCROLLBAR_WIDTH;
            int thumbH = Math.max(12, viewHeight * viewHeight / layout.height());
            int thumbY = viewTop + (viewHeight - thumbH) * scroll / Math.max(1, layout.height() - viewHeight);
            g.fill(trackX, viewTop, trackX + SCROLLBAR_WIDTH, viewTop + viewHeight, 0x22000000);
            g.fill(trackX, thumbY, trackX + SCROLLBAR_WIDTH, thumbY + thumbH, theme.quoteBar());
        }

        drawTooltip(g, mouseX, mouseY);
    }

    /** Link targets show where they go (the page's file name, like Markdown Manual); items show their name. */
    private void drawTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        if (!inContent(mouseX, mouseY)) {
            return;
        }

        layout.interactiveAt(mouseX - columnX, mouseY - viewTop + scroll).ifPresent(element -> {
            String link = switch (element) {
                case Element.Text t -> t.link();
                case Element.Icon icon -> icon.link();
                default -> null;
            };
            if (link == null) {
                return;
            }

            Component tooltip = switch (book.resolve(current, link)) {
                case LinkTarget.Page page -> Component.literal(page.node().sourcePath() != null ? page.node().sourcePath() : page.node().title());
                case LinkTarget.External external -> Component.literal(external.url());
                case LinkTarget.OtherBook other -> Component.literal(other.bookId() + "/" + other.pagePath());
                case LinkTarget.Reference ref -> ItemLookup.stack(ref.id(), 1).getHoverName();
                case LinkTarget.Broken broken -> Component.literal(broken.reason()).withStyle(net.minecraft.ChatFormatting.RED);
            };
            g.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        });
    }

    // ---- input ----

    private boolean inContent(double x, double y) {
        return x >= contentX && x < contentX + contentW && y >= viewTop && y < viewTop + viewHeight;
    }

    private boolean inSidebar(double x, double y) {
        return x >= panelX && x < panelX + sidebarW && y >= panelY + 22 && y < panelY + panelH;
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        double x = event.x();
        double y = event.y();

        // Right-clicking the search box clears it (a common convention in other mods' text fields).
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && search.isMouseOver(x, y)) {
            clearSearch();
            return true;
        }

        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && y >= panelY && y < panelY + HEADER_HEIGHT) {
            for (Crumb crumb : crumbs) {
                if (x >= crumb.x() && x < crumb.x() + crumb.width()) {
                    navigate(crumb.node(), null, true);
                    return true;
                }
            }
        }

        if (inContent(x, y)) {
            layout.linkAt((int) x - columnX, (int) y - viewTop + scroll).ifPresent(element -> {
                String destination = element instanceof Element.Text t ? t.link() : ((Element.Icon) element).link();
                follow(destination);
            });
            return true;
        }

        if (inSidebar(x, y)) {
            int row = ((int) y - (panelY + 22) + tocScroll) / TOC_ROW;
            boolean searching = !search.getValue().isBlank();
            if (row >= 0 && row < (searching ? hits.size() : toc.size())) {
                BookNode node = searching ? hits.get(row).node() : toc.get(row).node();

                // The arrow only opens or closes; the rest of the row goes to the page (which also opens it).
                if (!searching && expandable(node)) {
                    int ax = arrowX(toc.get(row).depth());
                    if (x >= ax - 2 && x < ax + ARROW_GUTTER) {
                        toggle(node);
                        return true;
                    }
                }

                navigate(node, null, true);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (inSidebar(mouseX, mouseY)) {
            int rows = search.getValue().isBlank() ? toc.size() : hits.size();
            int max = Math.max(0, rows * TOC_ROW - (panelH - 26));
            tocScroll = Math.max(0, Math.min(max, tocScroll - (int) (scrollY * TOC_ROW)));
            return true;
        }

        scroll -= (int) (scrollY * 18);
        clampScroll();
        return true;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        // Typing in the search box wins over navigation shortcuts.
        if (search.isFocused()) {
            return super.keyPressed(event);
        }

        switch (event.key()) {
            case 259 /* GLFW_KEY_BACKSPACE */ -> back();
            case 266 /* PAGE_UP */ -> scrollBy(-viewHeight + 20);
            case 267 /* PAGE_DOWN */ -> scrollBy(viewHeight - 20);
            case 268 /* HOME */ -> scrollBy(-layout.height());
            case 269 /* END */ -> scrollBy(layout.height());
            default -> {
                return super.keyPressed(event);
            }
        }

        return true;
    }

    private void scrollBy(int delta) {
        scroll += delta;
        clampScroll();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Resizing rebuilds every widget and re-wraps the page for the new width. Without this, whatever was typed in
     * the search box would be lost and the page would jump, so keep the query and the reading position (as a
     * fraction of the page, since the page height changes with the width).
     */
    @Override
    public void resize(int width, int height) {
        String query = search == null ? "" : search.getValue();
        boolean searchFocused = search != null && search.isFocused();
        double fraction = layout == null || layout.height() <= viewHeight ? 0 : scroll / (double) (layout.height() - viewHeight);

        super.resize(width, height);

        search.setValue(query);
        if (searchFocused) {
            setFocused(search);
        }
        scroll = (int) Math.round(fraction * Math.max(0, layout.height() - viewHeight));
        clampScroll();
    }
}
