package com.chimericdream.opus.core.book;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.model.Inline;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A fully loaded book: the page tree plus the indexes built from it. Create one with {@link BookLoader}. */
public final class Book {
    private static final Pattern SCHEME = Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.\\-]*):(.*)$", Pattern.DOTALL);
    private static final Pattern NUMERIC_PREFIX = Pattern.compile("^(\\d+)[-_.](?=.)");

    private final String id;
    private final BookMeta meta;
    private final BookNode root;
    private final List<BookNode> nodes = new ArrayList<>();
    private final Map<String, BookNode> byId = new HashMap<>();
    private final Map<String, BookNode> byRawPath = new HashMap<>();
    private final Map<String, BookNode> byAlias = new HashMap<>();
    private final Map<String, List<BookNode>> tags = new TreeMap<>();
    private final SearchIndex search;

    Book(String id, BookMeta meta, BookNode root, Diagnostics diagnostics) {
        this.id = id;
        this.meta = meta;
        this.root = root;

        collect(root);
        for (BookNode node : nodes) {
            index(node, diagnostics);
        }
        tags.values().forEach(list -> list.sort(Comparator.comparing(BookNode::title, String.CASE_INSENSITIVE_ORDER)));
        this.search = new SearchIndex(nodes);
    }

    private void collect(BookNode node) {
        nodes.add(node);
        node.children().forEach(this::collect);
    }

    private void index(BookNode node, Diagnostics diagnostics) {
        if (byId.putIfAbsent(node.id(), node) != null) {
            diagnostics.error(
                node.sourcePath() == null ? node.dirPath() : node.sourcePath(), 0,
                "id '" + node.id() + "' is already used by " + describe(byId.get(node.id()))
            );
        }

        if (node.isChapter()) {
            byRawPath.putIfAbsent(node.dirPath(), node);
            byRawPath.putIfAbsent(node.dirPath().isEmpty() ? "index" : node.dirPath() + "/index", node);
        } else if (node.sourcePath() != null) {
            byRawPath.putIfAbsent(stripMd(node.sourcePath()), node);
        }

        for (String alias : node.aliases()) {
            if (byAlias.putIfAbsent(alias, node) != null) {
                diagnostics.warn(node.sourcePath(), 0, "alias '" + alias + "' is already used by " + describe(byAlias.get(alias)));
            }
        }

        for (String tag : node.tags()) {
            tags.computeIfAbsent(tag, t -> new ArrayList<>()).add(node);
        }
    }

    private static String describe(BookNode node) {
        return node.sourcePath() != null ? node.sourcePath() : "folder '" + node.dirPath() + "'";
    }

    public String id() {
        return id;
    }

    public BookMeta meta() {
        return meta;
    }

    public BookNode root() {
        return root;
    }

    /** Every node in reading order (depth-first, siblings in their sorted order), starting with the root. */
    public List<BookNode> nodes() {
        return Collections.unmodifiableList(nodes);
    }

    public BookNode find(String nodeId) {
        BookNode node = byId.get(nodeId);
        return node != null ? node : byAlias.get(nodeId);
    }

    /** tag (lower-case) to the pages carrying it, sorted by title. */
    public Map<String, List<BookNode>> tags() {
        return Collections.unmodifiableMap(tags);
    }

    public List<SearchIndex.Hit> search(String query) {
        return search.search(query);
    }

    /** Root-first chain of ancestors ending at {@code node} itself. */
    public List<BookNode> breadcrumb(BookNode node) {
        Deque<BookNode> chain = new ArrayDeque<>();
        for (BookNode n = node; n != null; n = n.parent()) {
            chain.addFirst(n);
        }

        return List.copyOf(chain);
    }

    /**
     * Resolves a Markdown link destination found on {@code from}.
     *
     * <ul>
     *   <li>{@code https://...}, {@code mailto:...} - external</li>
     *   <li>{@code item:minecraft:hopper} (also {@code block:}, {@code entity:}, {@code fluid:}, {@code tag:}) - game object</li>
     *   <li>{@code book:ns:name/path/to/page#anchor} - a page in this or another book</li>
     *   <li>{@code ./page.md}, {@code ../chapter/}, {@code /from/root}, {@code #anchor} - a page of this book;
     *       the {@code .md} extension and numeric filename prefixes are optional</li>
     * </ul>
     */
    public LinkTarget resolve(BookNode from, String destination) {
        if (destination == null || destination.isBlank()) {
            return new LinkTarget.Broken("empty link");
        }

        String dest = destination.strip();
        Matcher scheme = SCHEME.matcher(dest);
        if (scheme.matches()) {
            String kind = scheme.group(1).toLowerCase(Locale.ROOT);
            String rest = scheme.group(2);
            return switch (kind) {
                case "http", "https", "mailto" -> new LinkTarget.External(dest);
                case "item", "block", "entity", "fluid", "tag" -> new LinkTarget.Reference(kind, rest);
                case "book" -> resolveBookLink(rest);
                default -> new LinkTarget.Broken("unsupported link scheme '" + kind + ":'");
            };
        }

        String path = dest;
        String anchor = null;
        int hash = dest.indexOf('#');
        if (hash >= 0) {
            path = dest.substring(0, hash);
            anchor = dest.substring(hash + 1);
            if (anchor.isEmpty()) {
                anchor = null;
            }
        }

        if (path.isEmpty()) {
            return new LinkTarget.Page(from, anchor);
        }

        return resolveLocal(from, path, anchor);
    }

    private LinkTarget resolveBookLink(String rest) {
        String anchor = null;
        int hash = rest.indexOf('#');
        if (hash >= 0) {
            anchor = rest.substring(hash + 1).isEmpty() ? null : rest.substring(hash + 1);
            rest = rest.substring(0, hash);
        }

        int slash = rest.indexOf('/');
        String bookId = slash < 0 ? rest : rest.substring(0, slash);
        String pagePath = slash < 0 ? "" : rest.substring(slash + 1);
        if (bookId.isEmpty()) {
            return new LinkTarget.Broken("book link needs a book id (book:namespace:name/page)");
        }

        if (bookId.equals(id)) {
            return resolveLocal(root, "/" + pagePath, anchor);
        }

        return new LinkTarget.OtherBook(bookId, pagePath, anchor);
    }

    private LinkTarget resolveLocal(BookNode from, String path, String anchor) {
        String base = path.startsWith("/") ? "" : from.dirPath();
        String normalized = normalize(base, path);
        if (normalized == null) {
            return new LinkTarget.Broken("link '" + path + "' points outside the book");
        }

        normalized = stripMd(normalized);
        BookNode node = byRawPath.get(normalized);
        if (node == null) {
            node = byId.get(stripDecorations(normalized));
        }
        if (node == null) {
            node = byAlias.get(normalized);
        }
        if (node == null) {
            return new LinkTarget.Broken("no page '" + path + "' in this book");
        }

        return new LinkTarget.Page(node, anchor);
    }

    /** Checks every link in every page; broken links are errors, unknown anchors are warnings. */
    void validateLinks(Diagnostics diagnostics) {
        for (BookNode node : nodes) {
            if (node.sourcePath() == null) {
                continue;
            }

            node.document().forEachInline(inline -> {
                if (!(inline instanceof Inline.Link link)) {
                    return;
                }

                switch (resolve(node, link.destination())) {
                    case LinkTarget.Broken b -> diagnostics.error(node.sourcePath(), link.line(), b.reason());
                    case LinkTarget.Page p -> {
                        if (p.anchor() != null && !p.node().document().hasAnchor(p.anchor())) {
                            diagnostics.warn(node.sourcePath(), link.line(),
                                "no heading '#" + p.anchor() + "' on " + describe(p.node()));
                        }
                    }
                    default -> {
                    }
                }
            });
        }
    }

    /** Joins {@code rel} onto {@code base} resolving {@code .} and {@code ..}; {@code null} if it escapes the root. */
    static String normalize(String base, String rel) {
        Deque<String> parts = new ArrayDeque<>();
        if (!base.isEmpty()) {
            for (String s : base.split("/")) {
                parts.addLast(s);
            }
        }

        for (String s : rel.split("/")) {
            if (s.isEmpty() || s.equals(".")) {
                continue;
            }
            if (s.equals("..")) {
                if (parts.isEmpty()) {
                    return null;
                }
                parts.removeLast();
            } else {
                parts.addLast(s);
            }
        }

        return String.join("/", parts);
    }

    static String stripMd(String path) {
        return path.toLowerCase(Locale.ROOT).endsWith(".md") ? path.substring(0, path.length() - 3) : path;
    }

    /** Removes numeric ordering prefixes from each segment and a trailing {@code index}. */
    static String stripDecorations(String path) {
        List<String> out = new ArrayList<>();
        for (String segment : path.split("/")) {
            if (!segment.isEmpty()) {
                out.add(stripPrefix(segment));
            }
        }
        if (!out.isEmpty() && out.get(out.size() - 1).equals("index")) {
            out.remove(out.size() - 1);
        }

        return String.join("/", out);
    }

    static String stripPrefix(String segment) {
        return NUMERIC_PREFIX.matcher(segment).replaceFirst("");
    }

    static Double numericPrefix(String segment) {
        Matcher m = NUMERIC_PREFIX.matcher(segment);
        return m.find() ? Double.valueOf(m.group(1)) : null;
    }
}
