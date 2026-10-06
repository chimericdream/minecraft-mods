package com.chimericdream.opus.core.book;

import com.chimericdream.opus.core.model.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/** A chapter (a folder) or a page (a Markdown file) of a book. */
public final class BookNode {
    public enum Kind {
        CHAPTER,
        PAGE
    }

    private final String id;
    private final Kind kind;
    private final String dirPath;
    private final String sourceName;

    private String title;
    private IconRef icon;
    private List<String> tags = List.of();
    private Double order;
    private boolean hidden;
    private List<String> requires = List.of();
    private List<String> aliases = List.of();
    private String summary;
    private String since;
    private String sourcePath;
    private Document document = Document.EMPTY;
    private BookNode parent;
    private final List<BookNode> children = new ArrayList<>();

    BookNode(String id, Kind kind, String dirPath, String sourceName) {
        this.id = id;
        this.kind = kind;
        this.dirPath = dirPath;
        this.sourceName = sourceName;
    }

    /** Stable id: the path with {@code .md}, numeric ordering prefixes and {@code index} removed. Root is "". */
    public String id() {
        return id;
    }

    public Kind kind() {
        return kind;
    }

    public boolean isChapter() {
        return kind == Kind.CHAPTER;
    }

    public String title() {
        return title;
    }

    public IconRef icon() {
        return icon;
    }

    public List<String> tags() {
        return tags;
    }

    /** Explicit order from frontmatter or a filename prefix; {@code null} sorts after ordered siblings. */
    public Double order() {
        return order;
    }

    public boolean hidden() {
        return hidden;
    }

    /** Advancement ids that must all be complete for this page to be shown. Empty means always visible. */
    public List<String> requires() {
        return requires;
    }

    public List<String> aliases() {
        return aliases;
    }

    public String summary() {
        return summary;
    }

    public String since() {
        return since;
    }

    /** The Markdown file this node came from, or {@code null} for a folder that has no {@code index.md}. */
    public String sourcePath() {
        return sourcePath;
    }

    public Document document() {
        return document;
    }

    public BookNode parent() {
        return parent;
    }

    public List<BookNode> children() {
        return Collections.unmodifiableList(children);
    }

    /** Raw folder path (as on disk) that relative links from this node resolve against. */
    String dirPath() {
        return dirPath;
    }

    /** Raw file or folder name, used as the final sort tiebreaker. */
    String sourceName() {
        return sourceName;
    }

    public boolean isUnlocked(Predicate<String> advancementDone) {
        return requires.stream().allMatch(advancementDone);
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setIcon(IconRef icon) {
        this.icon = icon;
    }

    void setTags(List<String> tags) {
        this.tags = List.copyOf(tags);
    }

    void setOrder(Double order) {
        this.order = order;
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    void setRequires(List<String> requires) {
        this.requires = List.copyOf(requires);
    }

    void setAliases(List<String> aliases) {
        this.aliases = List.copyOf(aliases);
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    void setSince(String since) {
        this.since = since;
    }

    void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    void setDocument(Document document) {
        this.document = document;
    }

    void addChild(BookNode child) {
        child.parent = this;
        children.add(child);
    }

    void sortChildren(java.util.Comparator<BookNode> comparator) {
        children.sort(comparator);
        children.forEach(c -> c.sortChildren(comparator));
    }

    @Override
    public String toString() {
        return kind + "[" + id + "]";
    }
}
