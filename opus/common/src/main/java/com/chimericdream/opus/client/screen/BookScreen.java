package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.client.OpusClient;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * The reading screen: a table of contents sidebar (with search) on the left and one scrolling page on the
 * right, with back-history and previous/next buttons. All text flow comes from {@link LayoutEngine}; this class
 * only owns geometry, input and navigation.
 *
 * <p>UNVERIFIED: never compiled. Input handlers use the {@code MouseButtonEvent}/{@code KeyEvent} signatures
 * seen in better-target-dummies' {@code MobPickerScreen}; {@code mouseScrolled}, {@code Button.builder},
 * {@code EditBox} and {@code ConfirmLinkScreen.confirmLinkNow} are standard but unchecked against 26.2. Drawing is
 * done in {@code extractBackground} (a hook the other mods' screens already override) so the widgets the base
 * class draws afterwards sit on top of the book.
 */
public class BookScreen extends Screen {
    private static final int MARGIN = 8;
    private static final int SIDEBAR_WIDTH = 124;
    private static final int PAD = 8;
    private static final int HEADER_HEIGHT = 22;
    private static final int FOOTER_HEIGHT = 24;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int TOC_ROW = 18;

    private record TocEntry(BookNode node, int depth) {
    }

    private final Book book;
    private final BookTheme theme = BookTheme.PARCHMENT;

    private BookNode current;
    private String pendingAnchor;
    private Layout layout;
    private int scroll;
    private int tocScroll;
    private final Deque<BookNode> backStack = new ArrayDeque<>();

    private List<TocEntry> toc = List.of();
    private List<SearchIndex.Hit> hits = List.of();

    private BookRenderer renderer;
    private EditBox search;
    private Button previous;
    private Button next;

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;
    private int contentX;
    private int contentW;
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
    }

    @Override
    protected void init() {
        panelX = MARGIN;
        panelY = MARGIN;
        panelW = width - 2 * MARGIN;
        panelH = height - 2 * MARGIN;
        contentX = panelX + SIDEBAR_WIDTH + PAD;
        contentW = panelW - SIDEBAR_WIDTH - 2 * PAD;
        viewTop = panelY + HEADER_HEIGHT;
        viewHeight = panelH - HEADER_HEIGHT - FOOTER_HEIGHT;

        renderer = new BookRenderer(font, theme);
        toc = buildToc();

        search = addRenderableWidget(new EditBox(font, panelX + 4, panelY + 4, SIDEBAR_WIDTH - 8, 14, Component.empty()));
        search.setHint(Component.translatable("opus.book.search").withStyle(EditBox.SEARCH_HINT_STYLE));
        search.setResponder(query -> {
            hits = book.search(query);
            tocScroll = 0;
        });

        previous = addRenderableWidget(Button.builder(Component.literal("<"), b -> step(-1))
            .bounds(contentX, panelY + panelH - FOOTER_HEIGHT + 2, 20, 18).build());
        next = addRenderableWidget(Button.builder(Component.literal(">"), b -> step(1))
            .bounds(contentX + contentW - 20, panelY + panelH - FOOTER_HEIGHT + 2, 20, 18).build());

        relayout();
    }

    // ---- navigation ----

    private void navigate(BookNode node, String anchor, boolean remember) {
        if (remember && node != current) {
            backStack.push(current);
        }

        current = node;
        pendingAnchor = anchor;
        scroll = 0;
        relayout();
    }

    private void back() {
        if (!backStack.isEmpty()) {
            navigate(backStack.pop(), null, false);
        }
    }

    /** Previous/next page in reading order, skipping hidden pages. */
    private void step(int delta) {
        List<BookNode> order = book.nodes().stream().filter(n -> toc.stream().anyMatch(e -> e.node() == n)).toList();
        int index = order.indexOf(current) + delta;
        if (index >= 0 && index < order.size()) {
            navigate(order.get(index), null, true);
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

        LayoutEngine engine = new LayoutEngine(new MinecraftTextMetrics(font), LayoutConfig.defaults(), WidgetSizer.DEFAULT);
        layout = engine.layoutPage(current.title(), current.document(), contentW - SCROLLBAR_WIDTH - 2);

        if (pendingAnchor != null) {
            scroll = layout.anchorY(pendingAnchor).orElse(0);
            pendingAnchor = null;
        }
        clampScroll();

        if (previous != null) {
            previous.active = indexInToc(current) > 0;
            next.active = indexInToc(current) >= 0 && indexInToc(current) < toc.size() - 1;
        }
    }

    private int indexInToc(BookNode node) {
        for (int i = 0; i < toc.size(); i++) {
            if (toc.get(i).node() == node) {
                return i;
            }
        }

        return -1;
    }

    private void clampScroll() {
        scroll = Math.max(0, Math.min(scroll, Math.max(0, layout.height() - viewHeight)));
    }

    // ---- table of contents ----

    private List<TocEntry> buildToc() {
        List<TocEntry> out = new ArrayList<>();
        collect(book.root(), 0, out);
        return out;
    }

    private void collect(BookNode node, int depth, List<TocEntry> out) {
        if (node.hidden() || !node.isUnlocked(this::advancementDone)) {
            return;
        }

        out.add(new TocEntry(node, depth));
        node.children().forEach(child -> collect(child, depth + 1, out));
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
        g.fill(panelX, panelY, panelX + SIDEBAR_WIDTH, panelY + panelH, theme.sidebar());

        drawSidebar(g, mouseX, mouseY);
        drawHeader(g);
        drawContent(g, mouseX, mouseY);
    }

    private void drawSidebar(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int top = panelY + 22;
        int bottom = panelY + panelH - 4;
        g.enableScissor(panelX, top, panelX + SIDEBAR_WIDTH, bottom);

        boolean searching = !search.getValue().isBlank();
        int count = searching ? hits.size() : toc.size();
        for (int i = 0; i < count; i++) {
            int y = top + i * TOC_ROW - tocScroll;
            if (y + TOC_ROW < top || y > bottom) {
                continue;
            }

            BookNode node = searching ? hits.get(i).node() : toc.get(i).node();
            int depth = searching ? 0 : toc.get(i).depth();
            boolean selected = node == current;
            boolean hovered = mouseX >= panelX && mouseX < panelX + SIDEBAR_WIDTH && mouseY >= y && mouseY < y + TOC_ROW && mouseY >= top && mouseY < bottom;

            if (selected || hovered) {
                g.fill(panelX, y, panelX + SIDEBAR_WIDTH, y + TOC_ROW, selected ? theme.sidebarSelected() : 0x22000000);
            }

            int x = panelX + 4 + Math.min(depth, 4) * 6;
            IconRef icon = node.icon();
            if (icon != null && icon.kind() == IconRef.Kind.ITEM) {
                g.item(ItemLookup.stack(icon.id(), 1), x, y + 1);
                x += 18;
            }
            // TODO(icons): IconRef.Kind.TEXTURE - draw the texture with blit once a loader for it exists.

            String title = font.plainSubstrByWidth(node.title(), panelX + SIDEBAR_WIDTH - x - 4);
            g.text(font, Component.literal(title), x, y + (TOC_ROW - font.lineHeight) / 2, theme.ink(), false);
        }

        g.disableScissor();
    }

    private void drawHeader(GuiGraphicsExtractor g) {
        String crumbs = book.breadcrumb(current).stream().map(BookNode::title).collect(Collectors.joining("  >  "));
        String clipped = font.plainSubstrByWidth(crumbs, contentW);
        g.text(font, Component.literal(clipped), contentX, panelY + (HEADER_HEIGHT - font.lineHeight) / 2, theme.muted(), false);
    }

    private void drawContent(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.enableScissor(contentX, viewTop, contentX + contentW, viewTop + viewHeight);
        renderer.draw(g, layout, contentX, viewTop, scroll, viewHeight, mouseX, mouseY);
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

        layout.interactiveAt(mouseX - contentX, mouseY - viewTop + scroll).ifPresent(element -> {
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
        return x >= panelX && x < panelX + SIDEBAR_WIDTH && y >= panelY + 22 && y < panelY + panelH;
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }

        double x = event.x();
        double y = event.y();

        if (inContent(x, y)) {
            layout.linkAt((int) x - contentX, (int) y - viewTop + scroll).ifPresent(element -> {
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
}
