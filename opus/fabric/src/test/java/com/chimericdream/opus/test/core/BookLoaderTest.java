package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.Diagnostic;
import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.core.book.BookLoader;
import com.chimericdream.opus.core.book.BookMeta;
import com.chimericdream.opus.core.book.BookNode;
import com.chimericdream.opus.core.book.BookSource;
import com.chimericdream.opus.core.book.IconRef;
import com.chimericdream.opus.core.book.LinkTarget;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookLoaderTest {
    private final Diagnostics diag = new Diagnostics();

    private Book load(BookSource.MapSource src) {
        return BookLoader.load("opus:test", src, BookMeta.empty("Test Book"), diag);
    }

    private static BookSource.MapSource sample() {
        return new BookSource.MapSource()
            .with("index.md", "# Welcome\n\nHello.\n")
            .with("machines/index.md", "---\ntitle: Machines\nicon: hopper\norder: 2\n---\nAll the machines.\n")
            .with("machines/01-hoppers.md", "---\ntags: [Logistics, Items]\nsummary: Moves items.\n---\n# Hoppers\n\nText.\n")
            .with("machines/02-sorters.md", "---\ntitle: Sorters\nhidden: true\nrequires: [minecraft:story/mine_stone]\n---\nSorts.\n")
            .with("getting-started/index.md", "---\norder: 1\n---\nStart here.\n")
            .with("getting-started/install.md", "Install it.\n")
            .with("empty-folder/page.md", "Page in a folder with no index.\n")
            .with("_drafts/wip.md", "not loaded\n")
            .with("machines/image.png", "binary");
    }

    @Test
    void foldersBecomeChaptersAndFilesBecomePages() {
        Book book = load(sample());

        BookNode root = book.root();
        assertEquals("", root.id());
        assertEquals("Welcome", root.title(), "leading H1 becomes the title");
        assertEquals(List.of("getting-started", "machines", "empty-folder"), root.children().stream().map(BookNode::id).toList());

        BookNode machines = book.find("machines");
        assertTrue(machines.isChapter());
        assertEquals("Machines", machines.title());
        assertEquals(IconRef.parse("item:minecraft:hopper"), machines.icon());
        assertEquals(List.of("machines/hoppers", "machines/sorters"), machines.children().stream().map(BookNode::id).toList());
    }

    @Test
    void synthesizedChaptersGetHumanizedTitles() {
        Book book = load(sample());

        BookNode folder = book.find("empty-folder");
        assertEquals("Empty Folder", folder.title());
        assertNull(folder.sourcePath());
        assertEquals("Page", book.find("empty-folder/page").title());
    }

    @Test
    void numericPrefixesOrderPagesButAreNotPartOfTheId() {
        Book book = load(sample());

        BookNode hoppers = book.find("machines/hoppers");
        assertNotNull(hoppers);
        assertEquals(1.0, hoppers.order());
        assertEquals("machines/01-hoppers.md", hoppers.sourcePath());
        assertEquals("Hoppers", hoppers.title());
    }

    @Test
    void frontmatterFieldsAreCarriedOntoTheNode() {
        Book book = load(sample());

        BookNode hoppers = book.find("machines/hoppers");
        assertEquals(List.of("logistics", "items"), hoppers.tags());
        assertEquals("Moves items.", hoppers.summary());

        BookNode sorters = book.find("machines/sorters");
        assertTrue(sorters.hidden());
        assertEquals(List.of("minecraft:story/mine_stone"), sorters.requires());
        assertFalse(sorters.isUnlocked(adv -> false));
        assertTrue(sorters.isUnlocked(adv -> true));
    }

    @Test
    void draftFoldersAndNonMarkdownFilesAreIgnored() {
        Book book = load(sample());

        assertNull(book.find("_drafts/wip"));
        assertNull(book.find("machines/image"));
        assertTrue(diag.all().isEmpty(), diag.all().toString());
    }

    @Test
    void pageTitleFallsBackToFilenameWhenThereIsNoHeading() {
        Book book = load(sample());

        assertEquals("Install", book.find("getting-started/install").title());
    }

    @Test
    void frontmatterTitleWinsAndHeadingIsKeptWhenTheyDiffer() {
        BookSource.MapSource src = new BookSource.MapSource()
            .with("a.md", "---\ntitle: Short\n---\n# A Longer Heading\n\nBody\n");
        Book book = load(src);

        BookNode a = book.find("a");
        assertEquals("Short", a.title());
        assertEquals(2, a.document().blocks().size());
    }

    @Test
    void tagIndexListsPagesCaseInsensitively() {
        Book book = load(sample());

        assertEquals(List.of("items", "logistics"), List.copyOf(book.tags().keySet()));
        assertEquals("machines/hoppers", book.tags().get("logistics").get(0).id());
    }

    @Test
    void breadcrumbRunsFromRootToNode() {
        Book book = load(sample());

        assertEquals(List.of("", "machines", "machines/hoppers"), book.breadcrumb(book.find("machines/hoppers")).stream().map(BookNode::id).toList());
    }

    @Test
    void unknownFrontmatterKeysWarnButXPrefixedKeysDoNot() {
        load(new BookSource.MapSource().with("a.md", "---\ntitel: typo\nx-note: fine\n---\nText\n"));

        assertEquals(1, diag.warningCount());
        assertTrue(diag.all().get(0).message().contains("titel"));
    }

    @Test
    void layeredSourcePrefersTheTranslationAndFallsBack() {
        BookSource en = new BookSource.MapSource().with("a.md", "# A\n\nEnglish\n").with("b.md", "# B\n\nEnglish B\n");
        BookSource de = new BookSource.MapSource().with("a.md", "# A (de)\n\nDeutsch\n");
        Book book = BookLoader.load("opus:test", BookSource.layered(de, en), BookMeta.empty("T"), diag);

        assertEquals("A (de)", book.find("a").title());
        assertEquals("B", book.find("b").title());
    }

    @Test
    void duplicateIdsAreReported() {
        load(new BookSource.MapSource().with("x.md", "one\n").with("x/index.md", "two\n"));

        assertTrue(diag.hasErrors());
        assertTrue(diag.all().get(0).message().contains("already used"));
    }

    @Test
    void bookMetaParsesTitleAndIcon() {
        BookMeta meta = BookMeta.parse("title: Field Guide\nicon: item:minecraft:book\nbogus: 1\n", "fallback", diag);

        assertEquals("Field Guide", meta.title());
        assertEquals("item:minecraft:book", meta.icon());
        assertEquals(1, diag.warningCount());
    }

    @Test
    void searchRanksTitleMatchesAboveBodyMatches() {
        BookSource.MapSource src = new BookSource.MapSource()
            .with("hoppers.md", "# Hoppers\n\nMove items around.\n")
            .with("chests.md", "# Chests\n\nChests can feed hoppers.\n")
            .with("misc.md", "# Misc\n\nNothing relevant.\n");
        Book book = load(src);

        var hits = book.search("hopp");
        assertEquals(List.of("hoppers", "chests"), hits.stream().map(h -> h.node().id()).toList());
        assertTrue(hits.get(1).snippet().contains("hoppers"));
        assertTrue(book.search("hopper nothing").isEmpty(), "every word must match");
        assertTrue(book.search("   ").isEmpty());
    }

    // ---- link resolution ----

    @Test
    void relativeLinksResolveWithAndWithoutExtensionAndPrefix() {
        Book book = load(sample());
        BookNode from = book.find("machines/hoppers");
        BookNode sorters = book.find("machines/sorters");

        for (String dest : List.of("./02-sorters.md", "02-sorters", "sorters", "../machines/sorters.md", "/machines/sorters")) {
            LinkTarget t = book.resolve(from, dest + "#top");
            LinkTarget.Page page = assertInstanceOf(LinkTarget.Page.class, t, dest + " -> " + t);
            assertEquals(sorters, page.node(), dest);
            assertEquals("top", page.anchor());
        }
    }

    @Test
    void linksToChaptersWorkByFolderOrIndex() {
        Book book = load(sample());
        BookNode from = book.find("getting-started/install");

        for (String dest : List.of("../machines/", "../machines", "../machines/index.md", "/machines")) {
            LinkTarget.Page page = assertInstanceOf(LinkTarget.Page.class, book.resolve(from, dest), dest);
            assertEquals("machines", page.node().id());
        }

        assertEquals("", ((LinkTarget.Page) book.resolve(from, "../index.md")).node().id());
    }

    @Test
    void anchorOnlyLinkStaysOnTheSamePage() {
        Book book = load(sample());
        BookNode here = book.find("machines/hoppers");

        LinkTarget.Page page = (LinkTarget.Page) book.resolve(here, "#usage");
        assertEquals(here, page.node());
        assertEquals("usage", page.anchor());
    }

    @Test
    void aliasesResolveLikePageNames() {
        Book book = load(new BookSource.MapSource()
            .with("new-name.md", "---\naliases: [old-name]\n---\nText\n")
            .with("other.md", "[x](old-name)\n"));

        assertTrue(diag.all().isEmpty(), diag.all().toString());
        assertEquals("new-name", ((LinkTarget.Page) book.resolve(book.find("other"), "old-name")).node().id());
    }

    @Test
    void schemeLinksResolveToExternalReferenceAndOtherBook() {
        Book book = load(sample());
        BookNode from = book.root();

        assertInstanceOf(LinkTarget.External.class, book.resolve(from, "https://example.com/x"));
        assertEquals(new LinkTarget.Reference("item", "minecraft:hopper"), book.resolve(from, "item:minecraft:hopper"));
        assertEquals(new LinkTarget.OtherBook("opus:other", "a/b", "c"), book.resolve(from, "book:opus:other/a/b#c"));

        LinkTarget.Page self = (LinkTarget.Page) book.resolve(from, "book:opus:test/machines/hoppers");
        assertEquals("machines/hoppers", self.node().id());

        assertInstanceOf(LinkTarget.Broken.class, book.resolve(from, "ftp://nope"));
        assertInstanceOf(LinkTarget.Broken.class, book.resolve(from, ""));
        assertInstanceOf(LinkTarget.Broken.class, book.resolve(from, "../../outside"));
        assertInstanceOf(LinkTarget.Broken.class, book.resolve(from, "nonexistent"));
    }

    @Test
    void brokenLinksAndAnchorsAreFoundAtLoadTime() {
        load(new BookSource.MapSource()
            .with("a.md", "---\ntitle: A\n---\n\n[ok](b.md#real) [bad page](missing.md)\n\n[bad anchor](b.md#nope)\n")
            .with("b.md", "# B\n\n## Real\n"));

        List<Diagnostic> all = diag.all();
        assertEquals(1, diag.errorCount(), all.toString());
        assertEquals(1, diag.warningCount(), all.toString());

        Diagnostic error = all.stream().filter(d -> d.severity() == Diagnostic.Severity.ERROR).findFirst().orElseThrow();
        assertEquals("a.md", error.path());
        assertEquals(5, error.line());
        assertTrue(error.message().contains("missing.md"));
    }
}
