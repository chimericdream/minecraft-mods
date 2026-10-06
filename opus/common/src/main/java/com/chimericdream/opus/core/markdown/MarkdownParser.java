package com.chimericdream.opus.core.markdown;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.frontmatter.YamlSupport;
import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Document;
import com.chimericdream.opus.core.model.Inline;
import com.chimericdream.opus.core.model.Style;
import com.chimericdream.opus.core.widget.WidgetSpecs;
import com.chimericdream.opus.core.widget.WidgetTypes;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.task.list.items.TaskListItemMarker;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.SourceSpan;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.IncludeSourceSpans;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Markdown (CommonMark + GFM tables, strikethrough and task lists) into Opus's own {@link Document}
 * model. Layout and rendering never see commonmark types, so the parser library can change without touching them.
 *
 * <p>Opus-specific syntax handled here:
 * <ul>
 *   <li>{@code > [!NOTE] Title} callouts</li>
 *   <li>fenced blocks whose info string is a registered widget type ({@code recipe}, {@code item}, ...)</li>
 *   <li>{@code ![](item:minecraft:diamond)} inline icons</li>
 *   <li>{@code ## Heading {#custom-id}} explicit heading anchors</li>
 * </ul>
 */
public final class MarkdownParser {
    private static final Parser PARSER = Parser.builder()
        .extensions(List.of(
            org.commonmark.ext.gfm.tables.TablesExtension.create(),
            StrikethroughExtension.create(),
            TaskListItemsExtension.create()
        ))
        .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
        .build();

    private static final Pattern CALLOUT_MARKER = Pattern.compile("^\\[!([A-Za-z]+)]\\s*(.*)$");
    private static final Pattern EXPLICIT_ANCHOR = Pattern.compile("\\s*\\{#([A-Za-z0-9_\\-:.]+)}\\s*$");
    private static final Set<String> ICON_SCHEMES = Set.of("item", "block", "entity");

    private MarkdownParser() {
    }

    /**
     * @param path      used only in diagnostics
     * @param firstLine 1-based line of {@code markdown}'s first line within its file (after frontmatter)
     */
    public static Document parse(String markdown, String path, int firstLine, Diagnostics diagnostics) {
        Node root = PARSER.parse(markdown);
        return new Document(new Converter(path, firstLine, diagnostics).blocks(root));
    }

    /** GitHub-style heading slug: lower-case, punctuation dropped, spaces to hyphens. */
    public static String slug(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                sb.append(c);
            } else if (c == ' ') {
                sb.append('-');
            }
        }

        String slug = sb.toString();
        return slug.isEmpty() ? "section" : slug;
    }

    private static final class Converter {
        private final String path;
        private final int firstLine;
        private final Diagnostics diagnostics;
        private final Set<String> anchors = new HashSet<>();

        Converter(String path, int firstLine, Diagnostics diagnostics) {
            this.path = path;
            this.firstLine = firstLine;
            this.diagnostics = diagnostics;
        }

        List<Block> blocks(Node parent) {
            List<Block> out = new ArrayList<>();
            for (Node child = parent.getFirstChild(); child != null; child = child.getNext()) {
                convertBlock(child, out);
            }

            return out;
        }

        private void convertBlock(Node node, List<Block> out) {
            if (node instanceof Heading h) {
                out.add(heading(h));
            } else if (node instanceof Paragraph p) {
                out.add(new Block.Paragraph(inlines(p, Style.PLAIN)));
            } else if (node instanceof BulletList l) {
                out.add(list(l, false, 1));
            } else if (node instanceof OrderedList l) {
                out.add(list(l, true, l.getMarkerStartNumber() == null ? 1 : l.getMarkerStartNumber()));
            } else if (node instanceof BlockQuote q) {
                out.add(quote(q));
            } else if (node instanceof FencedCodeBlock f) {
                out.add(fenced(f));
            } else if (node instanceof IndentedCodeBlock c) {
                out.add(new Block.Code("", stripTrailingNewline(c.getLiteral())));
            } else if (node instanceof ThematicBreak) {
                out.add(new Block.Rule());
            } else if (node instanceof TableBlock t) {
                out.add(table(t));
            } else if (node instanceof HtmlBlock) {
                diagnostics.warn(path, lineOf(node), "raw HTML is not supported and was ignored");
            }
            // Link reference definitions and anything unknown render nothing.
        }

        private Block.Heading heading(Heading h) {
            List<Inline> content = inlines(h, Style.PLAIN);
            String explicit = null;

            if (!content.isEmpty() && content.get(content.size() - 1) instanceof Inline.Text last) {
                Matcher m = EXPLICIT_ANCHOR.matcher(last.text());
                if (m.find()) {
                    explicit = m.group(1);
                    content = new ArrayList<>(content);
                    content.set(content.size() - 1, new Inline.Text(last.text().substring(0, m.start()), last.style()));
                }
            }

            String base = explicit != null ? explicit : slug(Inline.plainText(content));
            String anchor = base;
            int n = 1;
            while (!anchors.add(anchor)) {
                anchor = base + "-" + n++;
            }

            return new Block.Heading(h.getLevel(), anchor, content);
        }

        private Block.ListBlock list(Node list, boolean ordered, int start) {
            List<Block.ListItem> items = new ArrayList<>();
            for (Node child = list.getFirstChild(); child != null; child = child.getNext()) {
                if (child instanceof ListItem item) {
                    items.add(listItem(item));
                }
            }

            return new Block.ListBlock(ordered, start, items);
        }

        private Block.ListItem listItem(ListItem item) {
            Boolean checked = null;
            Node first = item.getFirstChild();
            if (first instanceof TaskListItemMarker m) {
                checked = m.isChecked();
                m.unlink();
            } else if (first instanceof Paragraph p && p.getFirstChild() instanceof TaskListItemMarker m) {
                checked = m.isChecked();
                m.unlink();
            }

            return new Block.ListItem(checked, blocks(item));
        }

        private Block quote(BlockQuote quote) {
            Node first = quote.getFirstChild();
            if (first instanceof Paragraph p) {
                Block callout = callout(quote, p);
                if (callout != null) {
                    return callout;
                }
            }

            return new Block.Quote(blocks(quote));
        }

        /** Detects {@code [!KIND] optional title} on the first line of a quote's first paragraph. */
        private Block callout(BlockQuote quote, Paragraph paragraph) {
            StringBuilder firstLineText = new StringBuilder();
            List<Node> firstLineNodes = new ArrayList<>();
            Node breakNode = null;

            for (Node n = paragraph.getFirstChild(); n != null; n = n.getNext()) {
                if (n instanceof SoftLineBreak || n instanceof HardLineBreak) {
                    breakNode = n;
                    break;
                }
                if (n instanceof Text t) {
                    firstLineText.append(t.getLiteral());
                } else {
                    return null;
                }
                firstLineNodes.add(n);
            }

            Matcher m = CALLOUT_MARKER.matcher(firstLineText.toString().strip());
            if (!m.matches()) {
                return null;
            }

            String kind = m.group(1).toLowerCase(Locale.ROOT);
            String title = m.group(2).isBlank() ? capitalize(kind) : m.group(2).strip();

            firstLineNodes.forEach(Node::unlink);
            if (breakNode != null) {
                breakNode.unlink();
            }
            if (paragraph.getFirstChild() == null) {
                paragraph.unlink();
            }

            return new Block.Callout(kind, title, blocks(quote));
        }

        private Block fenced(FencedCodeBlock fence) {
            String info = fence.getInfo() == null ? "" : fence.getInfo().strip();
            String type = info.isEmpty() ? "" : info.split("\\s+")[0];
            String body = stripTrailingNewline(fence.getLiteral());

            if (!WidgetTypes.isWidget(type)) {
                return new Block.Code(type, body);
            }

            int line = lineOf(fence);
            Map<String, Object> props = new LinkedHashMap<>();
            String error = null;
            try {
                Object loaded = YamlSupport.load(body);
                if (loaded instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> e : map.entrySet()) {
                        props.put(String.valueOf(e.getKey()), e.getValue());
                    }
                } else if (loaded != null) {
                    props.put("value", loaded);
                }
            } catch (YamlSupport.YamlException e) {
                error = "invalid YAML in " + type + " block: " + e.getMessage();
            }

            if (error == null) {
                error = WidgetSpecs.validate(type, props);
            }
            if (error != null) {
                diagnostics.error(path, line, error);
            }

            return new Block.Widget(type, props, line, error);
        }

        private Block table(TableBlock table) {
            List<Block.Align> aligns = new ArrayList<>();
            List<List<Inline>> header = new ArrayList<>();
            List<List<List<Inline>>> rows = new ArrayList<>();

            for (Node section = table.getFirstChild(); section != null; section = section.getNext()) {
                boolean isHead = section instanceof TableHead;
                if (!isHead && !(section instanceof TableBody)) {
                    continue;
                }

                for (Node row = section.getFirstChild(); row != null; row = row.getNext()) {
                    if (!(row instanceof TableRow)) {
                        continue;
                    }

                    List<List<Inline>> cells = new ArrayList<>();
                    for (Node cell = row.getFirstChild(); cell != null; cell = cell.getNext()) {
                        if (cell instanceof TableCell tc) {
                            cells.add(inlines(tc, isHead ? Style.PLAIN.withBold() : Style.PLAIN));
                            if (isHead) {
                                aligns.add(alignOf(tc.getAlignment()));
                            }
                        }
                    }

                    if (isHead) {
                        header.addAll(cells);
                    } else {
                        rows.add(cells);
                    }
                }
            }

            return new Block.Table(aligns, header, rows);
        }

        private static Block.Align alignOf(TableCell.Alignment alignment) {
            if (alignment == null) {
                return Block.Align.LEFT;
            }

            return switch (alignment) {
                case CENTER -> Block.Align.CENTER;
                case RIGHT -> Block.Align.RIGHT;
                default -> Block.Align.LEFT;
            };
        }

        List<Inline> inlines(Node parent, Style style) {
            List<Inline> out = new ArrayList<>();
            for (Node n = parent.getFirstChild(); n != null; n = n.getNext()) {
                convertInline(n, style, out);
            }

            return merge(out);
        }

        private void convertInline(Node node, Style style, List<Inline> out) {
            if (node instanceof Text t) {
                out.add(new Inline.Text(t.getLiteral(), style));
            } else if (node instanceof Code c) {
                out.add(new Inline.Text(c.getLiteral(), style.withCode()));
            } else if (node instanceof Emphasis) {
                out.addAll(inlines(node, style.withItalic()));
            } else if (node instanceof StrongEmphasis) {
                out.addAll(inlines(node, style.withBold()));
            } else if (node instanceof Strikethrough) {
                out.addAll(inlines(node, style.withStrikethrough()));
            } else if (node instanceof Link l) {
                out.add(new Inline.Link(l.getDestination(), l.getTitle(), inlines(l, style), lineOf(l)));
            } else if (node instanceof Image img) {
                out.add(image(img));
            } else if (node instanceof SoftLineBreak) {
                out.add(new Inline.LineBreak(false));
            } else if (node instanceof HardLineBreak) {
                out.add(new Inline.LineBreak(true));
            } else if (node instanceof HtmlInline) {
                // dropped, like block HTML
            } else if (node instanceof TaskListItemMarker) {
                // handled when the list item is converted
            } else {
                out.addAll(inlines(node, style));
            }
        }

        private Inline image(Image img) {
            String alt = Inline.plainText(inlines(img, Style.PLAIN));
            String dest = img.getDestination() == null ? "" : img.getDestination();
            int colon = dest.indexOf(':');
            if (colon > 0 && ICON_SCHEMES.contains(dest.substring(0, colon))) {
                return new Inline.Icon(dest.substring(0, colon), dest.substring(colon + 1), alt);
            }

            return new Inline.Image(dest, alt);
        }

        /** Merges adjacent text runs that share a style so later stages see fewer, longer runs. */
        private static List<Inline> merge(List<Inline> in) {
            List<Inline> out = new ArrayList<>();
            for (Inline inline : in) {
                if (inline instanceof Inline.Text t && !out.isEmpty()
                    && out.get(out.size() - 1) instanceof Inline.Text prev && prev.style().equals(t.style())) {
                    out.set(out.size() - 1, new Inline.Text(prev.text() + t.text(), t.style()));
                } else {
                    out.add(inline);
                }
            }

            return out;
        }

        private int lineOf(Node node) {
            List<SourceSpan> spans = node.getSourceSpans();
            return spans.isEmpty() ? 0 : firstLine + spans.get(0).getLineIndex();
        }

        private static String stripTrailingNewline(String s) {
            return s != null && s.endsWith("\n") ? s.substring(0, s.length() - 1) : (s == null ? "" : s);
        }

        private static String capitalize(String s) {
            return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }
}
