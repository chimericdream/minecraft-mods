package com.chimericdream.opus.core.book;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.frontmatter.Frontmatter;
import com.chimericdream.opus.core.markdown.MarkdownParser;
import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Document;
import com.chimericdream.opus.core.model.Inline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Builds a {@link Book} from a {@link BookSource}: folders become chapters, Markdown files become pages, and a
 * folder's {@code index.md} is that chapter's own page. Problems never abort the load; they are recorded in the
 * {@link Diagnostics} and the rest of the book still loads.
 */
public final class BookLoader {
    private static final Set<String> KNOWN_KEYS = Set.of(
        "title", "icon", "order", "tags", "summary", "description", "hidden", "requires", "aliases", "since"
    );

    private final Diagnostics diagnostics;
    private final Map<String, BookNode> chapters = new LinkedHashMap<>();

    private BookLoader(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public static Book load(String bookId, BookSource source, BookMeta meta, Diagnostics diagnostics) {
        return new BookLoader(diagnostics).run(bookId, source, meta);
    }

    private Book run(String bookId, BookSource source, BookMeta meta) {
        BookNode root = new BookNode("", BookNode.Kind.CHAPTER, "", "");
        root.setTitle(meta.title() != null ? meta.title() : bookId);
        root.setIcon(IconRef.parse(meta.icon()));
        root.setSummary(meta.description());
        chapters.put("", root);

        List<String> files = source.list().stream()
            .filter(f -> f.toLowerCase(Locale.ROOT).endsWith(".md"))
            .filter(f -> !isPrivate(f))
            .sorted()
            .toList();

        for (String file : files) {
            String text = source.readText(file).orElse("");
            String[] segments = file.split("/");
            String name = segments[segments.length - 1];
            String dir = String.join("/", List.of(segments).subList(0, segments.length - 1));

            BookNode chapter = chapter(dir);
            Frontmatter fm = Frontmatter.parse(text, diagnostics, file);

            BookNode node;
            if (name.equalsIgnoreCase("index.md")) {
                node = chapter;
            } else {
                String id = Book.stripDecorations(Book.stripMd(file));
                node = new BookNode(id, BookNode.Kind.PAGE, dir, name);
                node.setTitle(humanize(Book.stripPrefix(Book.stripMd(name))));
                node.setOrder(Book.numericPrefix(name));
                chapter.addChild(node);
            }

            fill(node, fm, file);
        }

        root.sortChildren(
            Comparator.comparingDouble((BookNode n) -> n.order() != null ? n.order() : Double.MAX_VALUE)
                .thenComparing(n -> n.sourceName().toLowerCase(Locale.ROOT))
        );

        Book book = new Book(bookId, meta, root, diagnostics);
        book.validateLinks(diagnostics);
        return book;
    }

    /** Files or folders starting with {@code _} or {@code .} are drafts/private and never loaded. */
    private static boolean isPrivate(String path) {
        for (String segment : path.split("/")) {
            if (segment.startsWith("_") || segment.startsWith(".")) {
                return true;
            }
        }

        return false;
    }

    private BookNode chapter(String dir) {
        BookNode existing = chapters.get(dir);
        if (existing != null) {
            return existing;
        }

        int slash = dir.lastIndexOf('/');
        String parentDir = slash < 0 ? "" : dir.substring(0, slash);
        String name = slash < 0 ? dir : dir.substring(slash + 1);

        BookNode parent = chapter(parentDir);
        BookNode node = new BookNode(Book.stripDecorations(dir), BookNode.Kind.CHAPTER, dir, name);
        node.setTitle(humanize(Book.stripPrefix(name)));
        node.setOrder(Book.numericPrefix(name));
        parent.addChild(node);
        chapters.put(dir, node);
        return node;
    }

    private void fill(BookNode node, Frontmatter fm, String path) {
        for (String key : fm.keys()) {
            if (!KNOWN_KEYS.contains(key) && !key.startsWith("x-")) {
                diagnostics.warn(path, 0, "unknown frontmatter key '" + key + "'");
            }
        }

        Document parsed = MarkdownParser.parse(fm.body(), path, fm.bodyStartLine(), diagnostics);
        List<Block> blocks = new ArrayList<>(parsed.blocks());

        // A leading '# Title' is the page title, so the renderer does not draw it twice.
        String fmTitle = fm.string("title");
        if (!blocks.isEmpty() && blocks.get(0) instanceof Block.Heading h && h.level() == 1) {
            String text = Inline.plainText(h.content()).strip();
            if (fmTitle == null || fmTitle.strip().equals(text)) {
                blocks.remove(0);
                if (fmTitle == null) {
                    fmTitle = text;
                }
            }
        }

        if (fmTitle != null && !fmTitle.isBlank()) {
            node.setTitle(fmTitle.strip());
        }

        String summary = fm.string("summary") != null ? fm.string("summary") : fm.string("description");
        if (summary != null) {
            node.setSummary(summary.strip());
        }
        if (fm.string("icon") != null) {
            node.setIcon(IconRef.parse(fm.string("icon")));
        }
        if (fm.number("order") != null) {
            node.setOrder(fm.number("order"));
        }

        node.setTags(fm.stringList("tags").stream().map(t -> t.toLowerCase(Locale.ROOT)).distinct().toList());
        node.setHidden(fm.bool("hidden", false));
        node.setRequires(fm.stringList("requires"));
        node.setAliases(fm.stringList("aliases"));
        node.setSince(fm.string("since"));
        node.setSourcePath(path);
        node.setDocument(new Document(blocks));
    }

    static String humanize(String name) {
        StringBuilder sb = new StringBuilder();
        for (String word : name.split("[-_\\s]+")) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        return sb.length() == 0 ? name : sb.toString();
    }
}
