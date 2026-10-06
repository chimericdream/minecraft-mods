package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.markdown.MarkdownParser;
import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Document;
import com.chimericdream.opus.core.model.Inline;
import com.chimericdream.opus.core.model.Style;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownParserTest {
    private static Document parse(String md) {
        return MarkdownParser.parse(md, "t.md", 1, new Diagnostics());
    }

    private static Document parse(String md, Diagnostics d) {
        return MarkdownParser.parse(md, "t.md", 1, d);
    }

    @Test
    void headingsGetUniqueSlugAnchors() {
        Document doc = parse("# Setup\n\n## Usage\n\n## Usage\n\n### What's new?\n");
        List<Block.Heading> hs = doc.headings();

        assertEquals(List.of("setup", "usage", "usage-1", "whats-new"), hs.stream().map(Block.Heading::anchor).toList());
    }

    @Test
    void explicitHeadingAnchorIsStrippedFromTheText() {
        Block.Heading h = parse("## Hoppers {#hop}\n").headings().get(0);

        assertEquals("hop", h.anchor());
        assertEquals("Hoppers", Inline.plainText(h.content()));
    }

    @Test
    void inlineStylesNestAndMerge() {
        Block.Paragraph p = (Block.Paragraph) parse("plain **bold *both*** ~~gone~~ `code`\n").blocks().get(0);
        List<Inline> in = p.content();

        assertEquals(new Inline.Text("plain ", Style.PLAIN), in.get(0));
        assertEquals(new Inline.Text("bold ", Style.PLAIN.withBold()), in.get(1));
        assertEquals(new Inline.Text("both", Style.PLAIN.withBold().withItalic()), in.get(2));
        assertEquals(new Inline.Text("gone", Style.PLAIN.withStrikethrough()), in.get(4));
        assertEquals(new Inline.Text("code", Style.PLAIN.withCode()), in.get(6));
    }

    @Test
    void linksKeepDestinationAndChildren() {
        Block.Paragraph p = (Block.Paragraph) parse("See [the **hopper**](../machines/hopper.md#use).\n").blocks().get(0);
        Inline.Link link = (Inline.Link) p.content().get(1);

        assertEquals("../machines/hopper.md#use", link.destination());
        assertEquals("the hopper", Inline.plainText(link.children()));
        assertEquals(1, link.line());
    }

    @Test
    void itemImageBecomesAnIconAndOthersStayImages() {
        Block.Paragraph p = (Block.Paragraph) parse("![Diamond](item:minecraft:diamond) ![pic](opus:textures/x.png)\n").blocks().get(0);

        Inline.Icon icon = assertInstanceOf(Inline.Icon.class, p.content().get(0));
        assertEquals("item", icon.kind());
        assertEquals("minecraft:diamond", icon.id());
        assertInstanceOf(Inline.Image.class, p.content().get(2));
    }

    @Test
    void bulletOrderedAndTaskLists() {
        Document doc = parse("- one\n- two\n\n3. three\n4. four\n\n- [x] done\n- [ ] todo\n");

        Block.ListBlock bullets = (Block.ListBlock) doc.blocks().get(0);
        assertFalse(bullets.ordered());
        assertEquals(2, bullets.items().size());
        assertNull(bullets.items().get(0).checked());

        Block.ListBlock ordered = (Block.ListBlock) doc.blocks().get(1);
        assertTrue(ordered.ordered());
        assertEquals(3, ordered.start());

        Block.ListBlock tasks = (Block.ListBlock) doc.blocks().get(2);
        assertEquals(Boolean.TRUE, tasks.items().get(0).checked());
        assertEquals(Boolean.FALSE, tasks.items().get(1).checked());
        assertEquals("done", Inline.plainText(((Block.Paragraph) tasks.items().get(0).blocks().get(0)).content()).strip());
    }

    @Test
    void calloutIsRecognisedWithOptionalTitle() {
        Document doc = parse("> [!WARNING] Careful now\n> Hot stuff.\n\n> [!NOTE]\n> Plain note.\n\n> normal quote\n");

        Block.Callout warning = (Block.Callout) doc.blocks().get(0);
        assertEquals("warning", warning.kind());
        assertEquals("Careful now", warning.title());
        assertEquals("Hot stuff.", Inline.plainText(((Block.Paragraph) warning.blocks().get(0)).content()));

        Block.Callout note = (Block.Callout) doc.blocks().get(1);
        assertEquals("Note", note.title());

        assertInstanceOf(Block.Quote.class, doc.blocks().get(2));
    }

    @Test
    void tablesCarryAlignmentHeaderAndRows() {
        Block.Table t = (Block.Table) parse("| A | B | C |\n|:--|:-:|--:|\n| 1 | 2 | 3 |\n| 4 | 5 | 6 |\n").blocks().get(0);

        assertEquals(List.of(Block.Align.LEFT, Block.Align.CENTER, Block.Align.RIGHT), t.aligns());
        assertEquals(3, t.header().size());
        assertEquals(2, t.rows().size());
        assertEquals("5", Inline.plainText(t.rows().get(1).get(1)));
    }

    @Test
    void codeBlocksKeepLanguageAndText() {
        Block.Code code = (Block.Code) parse("```java\nint x = 1;\n```\n").blocks().get(0);

        assertEquals("java", code.language());
        assertEquals("int x = 1;", code.text());
    }

    @Test
    void recipeFenceBecomesAValidatedWidget() {
        Document doc = parse("""
            ```recipe
            pattern: ["I I", "ICI", " I "]
            key: { I: iron_ingot, C: chest }
            result: hopper
            ```
            """);

        Block.Widget w = (Block.Widget) doc.blocks().get(0);
        assertEquals("recipe", w.type());
        assertNull(w.error());
        assertEquals(1, w.line());
    }

    @Test
    void invalidWidgetBodiesAreReportedNotThrown() {
        Diagnostics d = new Diagnostics();
        Document doc = parse("text\n\n```recipe\npattern: [\"I\"]\nresult: hopper\n```\n\n```item\nid: [broken\n```\n", d);

        Block.Widget bad = (Block.Widget) doc.blocks().get(1);
        assertTrue(bad.error().contains("key"), bad.error());
        assertEquals(3, bad.line());

        Block.Widget badYaml = (Block.Widget) doc.blocks().get(2);
        assertTrue(badYaml.error().startsWith("invalid YAML"));
        assertEquals(2, d.errorCount());
    }

    @Test
    void lineNumbersRespectTheFrontmatterOffset() {
        Diagnostics d = new Diagnostics();
        MarkdownParser.parse("\n```recipe\nresult: nothing\n```\n", "t.md", 5, d);

        assertEquals(6, d.all().get(0).line());
    }

    @Test
    void rawHtmlIsDroppedWithAWarning() {
        Diagnostics d = new Diagnostics();
        Document doc = parse("<div>hi</div>\n\ntext\n", d);

        assertEquals(1, doc.blocks().size());
        assertEquals(1, d.warningCount());
    }

    @Test
    void plainTextCoversHeadingsParagraphsAndTables() {
        Document doc = parse("# Title\n\nSome *words*.\n\n| H |\n|---|\n| cell |\n");

        String text = doc.plainText();
        assertTrue(text.contains("Title"));
        assertTrue(text.contains("Some words."));
        assertTrue(text.contains("cell"));
    }
}
