package com.chimericdream.opus.core.layout;

import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Document;
import com.chimericdream.opus.core.model.Inline;
import com.chimericdream.opus.core.model.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns a {@link Document} into positioned {@link Element}s for a given content width: word-wrapping, lists,
 * quotes, callouts, code, tables and widgets. It knows nothing about Minecraft; text is measured through
 * {@link TextMetrics}.
 */
public final class LayoutEngine {
    private static final String[] BULLETS = {"•", "-"};

    private final TextMetrics metrics;
    private final LayoutConfig cfg;
    private final WidgetSizer sizer;

    public LayoutEngine(TextMetrics metrics, LayoutConfig cfg, WidgetSizer sizer) {
        this.metrics = metrics;
        this.cfg = cfg;
        this.sizer = sizer;
    }

    public Layout layout(Document doc, int width) {
        Out out = new Out();
        int height = blocks(doc.blocks(), 0, 0, Math.max(width, 1), out);
        return new Layout(List.copyOf(out.elements), height, Map.copyOf(out.anchors));
    }

    /** Lays out a page with its title drawn as a top-level heading (anchor {@code top}). */
    public Layout layoutPage(String title, Document doc, int width) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(new Block.Heading(1, "top", List.of(new Inline.Text(title, Style.PLAIN))));
        blocks.addAll(doc.blocks());
        return layout(new Document(blocks), width);
    }

    private static final class Out {
        final List<Element> elements = new ArrayList<>();
        final Map<String, Integer> anchors = new HashMap<>();
        int listDepth = 0;
    }

    // ---- blocks ----

    private int blocks(List<Block> blocks, int x, int y, int w, Out out) {
        boolean first = true;
        for (Block block : blocks) {
            if (!first) {
                y += cfg.blockSpacing();
                if (block instanceof Block.Heading) {
                    y += cfg.headingSpacing();
                }
            }
            first = false;
            y = block(block, x, y, w, out);
        }

        return y;
    }

    private int block(Block block, int x, int y, int w, Out out) {
        return switch (block) {
            case Block.Heading h -> heading(h, x, y, w, out);
            case Block.Paragraph p -> inlines(p.content(), x, y, w, Element.TextRole.BODY, 1f, Style.PLAIN, Block.Align.LEFT, out);
            case Block.ListBlock l -> list(l, x, y, w, out);
            case Block.Quote q -> quote(q, x, y, w, out);
            case Block.Callout c -> callout(c, x, y, w, out);
            case Block.Code c -> code(c, x, y, w, out);
            case Block.Rule r -> {
                out.elements.add(new Element.Rect(x, y + 2, w, 1, Element.RectRole.RULE, null));
                yield y + 5;
            }
            case Block.Table t -> table(t, x, y, w, out);
            case Block.Widget wd -> widget(wd, x, y, w, out);
        };
    }

    private int heading(Block.Heading h, int x, int y, int w, Out out) {
        out.anchors.putIfAbsent(h.anchor(), y);
        float scale = cfg.headingScale(h.level());
        y = inlines(h.content(), x, y, w, Element.TextRole.HEADING, scale, Style.PLAIN.withBold(), Block.Align.LEFT, out);
        if (h.level() <= 2) {
            y += 1;
            out.elements.add(new Element.Rect(x, y, w, 1, Element.RectRole.HEADING_UNDERLINE, null));
            y += 1;
        }

        return y;
    }

    private int list(Block.ListBlock list, int x, int y, int w, Out out) {
        List<String> markers = new ArrayList<>();
        int markerWidth = 0;
        for (int i = 0; i < list.items().size(); i++) {
            Block.ListItem item = list.items().get(i);
            String marker;
            if (item.checked() != null) {
                marker = item.checked() ? "[x]" : "[ ]";
            } else if (list.ordered()) {
                marker = (list.start() + i) + ".";
            } else {
                marker = BULLETS[out.listDepth % BULLETS.length];
            }
            markers.add(marker);
            markerWidth = Math.max(markerWidth, metrics.width(marker, Style.PLAIN));
        }

        int indent = markerWidth + cfg.listGap();
        out.listDepth++;
        for (int i = 0; i < list.items().size(); i++) {
            if (i > 0) {
                y += cfg.listItemSpacing();
            }

            int top = y;
            y = blocks(list.items().get(i).blocks(), x + indent, y, Math.max(w - indent, 1), out);
            if (y == top) {
                y += metrics.lineHeight();
            }

            String marker = markers.get(i);
            int mw = metrics.width(marker, Style.PLAIN);
            out.elements.add(new Element.Text(x + markerWidth - mw, top, mw, metrics.lineHeight(), marker, Style.PLAIN, 1f, Element.TextRole.MUTED, null));
        }
        out.listDepth--;

        return y;
    }

    private int quote(Block.Quote quote, int x, int y, int w, Out out) {
        int top = y;
        y = blocks(quote.blocks(), x + cfg.quoteIndent(), y, Math.max(w - cfg.quoteIndent(), 1), out);
        out.elements.add(new Element.Rect(x, top, 2, y - top, Element.RectRole.QUOTE_BAR, null));
        return y;
    }

    private int callout(Block.Callout callout, int x, int y, int w, Out out) {
        int pad = cfg.boxPadding();
        int bar = 2;
        int top = y;
        int insert = out.elements.size();

        int innerX = x + bar + pad;
        int innerW = Math.max(w - bar - 2 * pad, 1);

        y += pad;
        y = inlines(List.of(new Inline.Text(callout.title(), Style.PLAIN.withBold())), innerX, y, innerW,
            Element.TextRole.CALLOUT_TITLE, 1f, Style.PLAIN, Block.Align.LEFT, out);
        if (!callout.blocks().isEmpty()) {
            y += cfg.lineSpacing() + 2;
            y = blocks(callout.blocks(), innerX, y, innerW, out);
        }
        y += pad;

        out.elements.add(insert, new Element.Rect(x, top, w, y - top, Element.RectRole.CALLOUT_BACKGROUND, callout.kind()));
        out.elements.add(insert + 1, new Element.Rect(x, top, bar, y - top, Element.RectRole.CALLOUT_BAR, callout.kind()));
        return y;
    }

    private int code(Block.Code code, int x, int y, int w, Out out) {
        int pad = cfg.boxPadding();
        int top = y;
        int insert = out.elements.size();
        Style style = Style.PLAIN.withCode();
        int innerW = Math.max(w - 2 * pad, 1);

        y += pad;
        boolean first = true;
        for (String raw : code.text().replace("\t", "    ").split("\n", -1)) {
            for (String chunk : wrapChars(raw, innerW, style)) {
                if (!first) {
                    y += cfg.lineSpacing();
                }
                first = false;
                int cw = metrics.width(chunk, style);
                out.elements.add(new Element.Text(x + pad, y, cw, metrics.lineHeight(), chunk, style, 1f, Element.TextRole.CODE, null));
                y += metrics.lineHeight();
            }
        }
        y += pad;

        out.elements.add(insert, new Element.Rect(x, top, w, y - top, Element.RectRole.CODE_BACKGROUND, code.language()));
        return y;
    }

    private int widget(Block.Widget widget, int x, int y, int w, Out out) {
        if (widget.error() != null) {
            String message = "[" + widget.type() + ": " + widget.error() + "]";
            return inlines(List.of(new Inline.Text(message, Style.PLAIN)), x, y, w, Element.TextRole.ERROR, 1f, Style.PLAIN, Block.Align.LEFT, out);
        }

        WidgetSizer.Size size = sizer.measure(widget, w);
        int bx = x + Math.max(0, (w - size.width()) / 2);
        out.elements.add(new Element.WidgetBox(bx, y, size.width(), size.height(), widget));
        return y + size.height();
    }

    // ---- tables ----

    private int table(Block.Table table, int x, int y, int w, Out out) {
        int cols = table.header().size();
        for (List<List<Inline>> row : table.rows()) {
            cols = Math.max(cols, row.size());
        }
        if (cols == 0) {
            return y;
        }

        List<List<List<Inline>>> rows = new ArrayList<>();
        if (!table.header().isEmpty()) {
            rows.add(table.header());
        }
        rows.addAll(table.rows());

        int pad = cfg.tablePadding();
        int[] colW = columnWidths(rows, cols, w, pad);
        int tableW = cols + 1;
        for (int c = 0; c < cols; c++) {
            tableW += colW[c] + 2 * pad;
        }

        int tableTop = y;
        out.elements.add(new Element.Rect(x, y, tableW, 1, Element.RectRole.TABLE_BORDER, null));
        y += 1;

        for (int r = 0; r < rows.size(); r++) {
            boolean header = r == 0 && !table.header().isEmpty();
            int rowStart = out.elements.size();
            int rowH = 0;
            int cx = x + 1;

            for (int c = 0; c < cols; c++) {
                List<List<Inline>> row = rows.get(r);
                if (c < row.size()) {
                    Block.Align align = c < table.aligns().size() ? table.aligns().get(c) : Block.Align.LEFT;
                    int end = inlines(row.get(c), cx + pad, y + pad, colW[c], Element.TextRole.BODY, 1f, Style.PLAIN, align, out);
                    rowH = Math.max(rowH, end - (y + pad));
                }
                cx += colW[c] + 2 * pad + 1;
            }

            rowH = Math.max(rowH, metrics.lineHeight()) + 2 * pad;
            if (header) {
                out.elements.add(rowStart, new Element.Rect(x + 1, y, tableW - 2, rowH, Element.RectRole.TABLE_HEADER, null));
            }

            y += rowH;
            out.elements.add(new Element.Rect(x, y, tableW, 1, Element.RectRole.TABLE_BORDER, null));
            y += 1;
        }

        int vx = x;
        for (int c = 0; c <= cols; c++) {
            out.elements.add(new Element.Rect(vx, tableTop, 1, y - tableTop, Element.RectRole.TABLE_BORDER, null));
            if (c < cols) {
                vx += colW[c] + 2 * pad + 1;
            }
        }

        return y;
    }

    private int[] columnWidths(List<List<List<Inline>>> rows, int cols, int w, int pad) {
        int[] natural = new int[cols];
        for (List<List<Inline>> row : rows) {
            for (int c = 0; c < row.size(); c++) {
                natural[c] = Math.max(natural[c], naturalWidth(row.get(c), Style.PLAIN));
            }
        }

        int available = Math.max(w - (cols + 1) - cols * 2 * pad, cols);
        int sum = 0;
        for (int n : natural) {
            sum += n;
        }

        int[] widths = new int[cols];
        for (int c = 0; c < cols; c++) {
            widths[c] = Math.max(natural[c], 1);
        }
        if (sum <= available) {
            return widths;
        }

        // Too wide: share the space in proportion to natural width, never narrower than a few characters.
        int min = Math.min(metrics.width("mmmm", Style.PLAIN), available / cols);
        for (int c = 0; c < cols; c++) {
            widths[c] = Math.max(min, (int) ((long) natural[c] * available / sum));
        }

        int total = 0;
        for (int v : widths) {
            total += v;
        }
        while (total > available) {
            int widest = 0;
            for (int c = 1; c < cols; c++) {
                if (widths[c] > widths[widest]) {
                    widest = c;
                }
            }
            if (widths[widest] <= 1) {
                break;
            }
            widths[widest]--;
            total--;
        }

        return widths;
    }

    private int naturalWidth(List<Inline> inlines, Style base) {
        int width = 0;
        for (Inline inline : inlines) {
            switch (inline) {
                case Inline.Text t -> width += metrics.width(t.text(), merge(base, t.style()));
                case Inline.Link l -> width += naturalWidth(l.children(), base);
                case Inline.Icon i -> width += cfg.iconSize();
                case Inline.Image i -> width += cfg.iconSize();
                case Inline.LineBreak b -> width += metrics.width(" ", base);
            }
        }

        return width;
    }

    // ---- inline text ----

    private enum PieceKind {
        WORD,
        SPACE,
        BREAK,
        ICON
    }

    private record Piece(PieceKind kind, String text, Style style, Element.TextRole role, String link, String iconKind, String iconId) {
    }

    private record Placed(int x, String text, Style style, Element.TextRole role, String link, int width, String iconKind, String iconId) {
        boolean sameAttributes(Piece p) {
            return iconKind == null && style.equals(p.style()) && role == p.role() && java.util.Objects.equals(link, p.link());
        }
    }

    /**
     * Word-wraps inline content into the column {@code [x, x+w)} starting at {@code y}.
     *
     * @return the y just below the last line (no trailing spacing)
     */
    private int inlines(List<Inline> content, int x, int y, int w, Element.TextRole baseRole, float scale, Style baseStyle, Block.Align align, Out out) {
        List<Piece> pieces = new ArrayList<>();
        flatten(content, baseStyle, baseRole, null, pieces);

        LineBuilder line = new LineBuilder(x, y, w, scale, align, out);
        for (Piece piece : pieces) {
            switch (piece.kind()) {
                case SPACE -> line.space(piece);
                case BREAK -> line.hardBreak();
                case ICON -> line.icon(piece);
                case WORD -> line.word(piece);
            }
        }

        return line.finish();
    }

    private void flatten(List<Inline> content, Style base, Element.TextRole role, String link, List<Piece> out) {
        for (Inline inline : content) {
            switch (inline) {
                case Inline.Text t -> {
                    Style style = merge(base, t.style());
                    Element.TextRole r = link != null ? Element.TextRole.LINK
                        : style.code() && role == Element.TextRole.BODY ? Element.TextRole.CODE : role;
                    splitWords(t.text(), style, r, link, out);
                }
                case Inline.Link l -> flatten(l.children(), base, role, l.destination(), out);
                case Inline.Icon i -> out.add(new Piece(PieceKind.ICON, i.alt(), base, role, link, i.kind(), i.id()));
                case Inline.Image i -> splitWords("[" + (i.alt() == null || i.alt().isBlank() ? "image" : i.alt()) + "]", base, Element.TextRole.MUTED, link, out);
                case Inline.LineBreak b -> out.add(b.hard()
                    ? new Piece(PieceKind.BREAK, "", base, role, null, null, null)
                    : new Piece(PieceKind.SPACE, " ", base, role, link, null, null));
            }
        }
    }

    private static void splitWords(String text, Style style, Element.TextRole role, String link, List<Piece> out) {
        int i = 0;
        while (i < text.length()) {
            boolean space = Character.isWhitespace(text.charAt(i));
            int j = i;
            while (j < text.length() && Character.isWhitespace(text.charAt(j)) == space) {
                j++;
            }

            out.add(space
                ? new Piece(PieceKind.SPACE, " ", style, role, link, null, null)
                : new Piece(PieceKind.WORD, text.substring(i, j), style, role, link, null, null));
            i = j;
        }
    }

    private static Style merge(Style a, Style b) {
        return new Style(a.bold() || b.bold(), a.italic() || b.italic(), a.strikethrough() || b.strikethrough(), a.code() || b.code());
    }

    /** Accumulates pieces into lines, flushing whenever the next word would not fit. */
    private final class LineBuilder {
        private final int x;
        private final int w;
        private final float scale;
        private final Block.Align align;
        private final Out out;
        private final List<Placed> placed = new ArrayList<>();

        private int y;
        private int cursor = 0;
        private int pendingSpace = 0;
        private int maxIconHeight = 0;
        private boolean anyLine = false;

        LineBuilder(int x, int y, int w, float scale, Block.Align align, Out out) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.scale = scale;
            this.align = align;
            this.out = out;
        }

        private int scaled(String text, Style style) {
            return (int) Math.ceil(metrics.width(text, style) * scale);
        }

        void space(Piece piece) {
            if (!placed.isEmpty() && pendingSpace == 0) {
                pendingSpace = scaled(" ", piece.style());
            }
        }

        void word(Piece piece) {
            String text = piece.text();
            while (!text.isEmpty()) {
                int wordW = scaled(text, piece.style());
                if (!placed.isEmpty() && cursor + pendingSpace + wordW > w) {
                    flush(false);
                }

                int room = w - (placed.isEmpty() ? 0 : cursor + pendingSpace);
                if (wordW > room && placed.isEmpty()) {
                    // A single word wider than the column: take as many characters as fit.
                    int fit = 1;
                    while (fit < text.length() && scaled(text.substring(0, fit + 1), piece.style()) <= w) {
                        fit++;
                    }
                    add(new Piece(PieceKind.WORD, text.substring(0, fit), piece.style(), piece.role(), piece.link(), null, null));
                    text = text.substring(fit);
                    if (!text.isEmpty()) {
                        flush(false);
                    }
                } else {
                    add(new Piece(PieceKind.WORD, text, piece.style(), piece.role(), piece.link(), null, null));
                    text = "";
                }
            }
        }

        private void add(Piece piece) {
            int wordW = scaled(piece.text(), piece.style());
            if (!placed.isEmpty()) {
                Placed prev = placed.get(placed.size() - 1);
                if (pendingSpace > 0 && prev.sameAttributes(piece)) {
                    String joined = prev.text() + " " + piece.text();
                    placed.set(placed.size() - 1, new Placed(prev.x(), joined, prev.style(), prev.role(), prev.link(), prev.width() + pendingSpace + wordW, null, null));
                    cursor += pendingSpace + wordW;
                    pendingSpace = 0;
                    return;
                }
            }

            int px = cursor + pendingSpace;
            placed.add(new Placed(px, piece.text(), piece.style(), piece.role(), piece.link(), wordW, null, null));
            cursor = px + wordW;
            pendingSpace = 0;
        }

        void icon(Piece piece) {
            int size = cfg.iconSize();
            if (!placed.isEmpty() && cursor + pendingSpace + size > w) {
                flush(false);
            }

            int px = placed.isEmpty() ? 0 : cursor + pendingSpace;
            placed.add(new Placed(px, piece.text(), piece.style(), piece.role(), piece.link(), size, piece.iconKind(), piece.iconId()));
            cursor = px + size;
            pendingSpace = 0;
            maxIconHeight = Math.max(maxIconHeight, size);
        }

        void hardBreak() {
            flush(true);
        }

        private void flush(boolean forced) {
            if (placed.isEmpty() && !forced) {
                return;
            }

            int textH = (int) Math.ceil(metrics.lineHeight() * scale);
            int lineH = Math.max(textH, maxIconHeight);
            if (anyLine) {
                y += cfg.lineSpacing();
            }

            int shift = switch (align) {
                case LEFT -> 0;
                case CENTER -> Math.max(0, (w - cursor) / 2);
                case RIGHT -> Math.max(0, w - cursor);
            };

            for (Placed p : placed) {
                if (p.iconKind() != null) {
                    out.elements.add(new Element.Icon(x + shift + p.x(), y + (lineH - p.width()) / 2, p.width(), p.width(), p.iconKind(), p.iconId(), p.link()));
                } else {
                    out.elements.add(new Element.Text(x + shift + p.x(), y + (lineH - textH) / 2, p.width(), textH, p.text(), p.style(), scale, p.role(), p.link()));
                }
            }

            y += lineH;
            anyLine = true;
            placed.clear();
            cursor = 0;
            pendingSpace = 0;
            maxIconHeight = 0;
        }

        int finish() {
            flush(false);
            return y;
        }
    }

    private List<String> wrapChars(String line, int width, Style style) {
        List<String> chunks = new ArrayList<>();
        if (line.isEmpty()) {
            chunks.add("");
            return chunks;
        }

        int start = 0;
        while (start < line.length()) {
            int end = start + 1;
            while (end < line.length() && metrics.width(line.substring(start, end + 1), style) <= width) {
                end++;
            }
            chunks.add(line.substring(start, end));
            start = end;
        }

        return chunks;
    }
}
