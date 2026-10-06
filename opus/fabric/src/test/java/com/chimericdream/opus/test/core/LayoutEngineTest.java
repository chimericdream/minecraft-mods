package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.layout.Element;
import com.chimericdream.opus.core.layout.Layout;
import com.chimericdream.opus.core.layout.LayoutConfig;
import com.chimericdream.opus.core.layout.LayoutEngine;
import com.chimericdream.opus.core.layout.TextMetrics;
import com.chimericdream.opus.core.layout.WidgetSizer;
import com.chimericdream.opus.core.markdown.MarkdownParser;
import com.chimericdream.opus.core.model.Document;
import com.chimericdream.opus.core.model.Style;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutEngineTest {
    /** 6px per character, 8px lines: easy arithmetic. */
    private static final TextMetrics FIXED = new TextMetrics() {
        @Override
        public int width(String text, Style style) {
            return text.length() * 6;
        }

        @Override
        public int lineHeight() {
            return 8;
        }
    };

    private final LayoutEngine engine = new LayoutEngine(FIXED, LayoutConfig.defaults(), WidgetSizer.DEFAULT);

    private Layout layout(String md, int width) {
        Document doc = MarkdownParser.parse(md, "t.md", 1, new Diagnostics());
        return engine.layout(doc, width);
    }

    private static List<Element.Text> texts(Layout l) {
        return l.elements().stream().filter(e -> e instanceof Element.Text).map(e -> (Element.Text) e).toList();
    }

    private static List<Element.Rect> rects(Layout l, Element.RectRole role) {
        return l.elements().stream().filter(e -> e instanceof Element.Rect r && r.role() == role).map(e -> (Element.Rect) e).toList();
    }

    @Test
    void emptyDocumentHasZeroHeight() {
        assertEquals(0, layout("", 100).height());
    }

    @Test
    void wordsWrapAtTheColumnWidthAndMergeIntoRuns() {
        // 60px = 10 characters per line
        Layout l = layout("aaaa bbbb cccc dddd\n", 60);

        List<Element.Text> t = texts(l);
        assertEquals(List.of("aaaa bbbb", "cccc dddd"), t.stream().map(Element.Text::text).toList());
        assertEquals(0, t.get(0).y());
        assertEquals(9, t.get(1).y(), "line height 8 + 1px line spacing");
        assertEquals(54, t.get(0).width());
        assertEquals(17, l.height());
    }

    @Test
    void aSingleWordWiderThanTheColumnIsSplit() {
        Layout l = layout("abcdefghijkl\n", 30);

        assertEquals(List.of("abcde", "fghij", "kl"), texts(l).stream().map(Element.Text::text).toList());
    }

    @Test
    void hardBreakStartsANewLine() {
        Layout l = layout("one  \ntwo\n", 200);

        assertEquals(List.of("one", "two"), texts(l).stream().map(Element.Text::text).toList());
        assertEquals(9, texts(l).get(1).y());
    }

    @Test
    void softBreakJoinsWithASpace() {
        Layout l = layout("one\ntwo\n", 200);

        assertEquals(List.of("one two"), texts(l).stream().map(Element.Text::text).toList());
    }

    @Test
    void paragraphsAreSeparatedByBlockSpacing() {
        Layout l = layout("first\n\nsecond\n", 200);

        List<Element.Text> t = texts(l);
        assertEquals(0, t.get(0).y());
        assertEquals(8 + LayoutConfig.defaults().blockSpacing(), t.get(1).y());
    }

    @Test
    void styleChangesSplitRunsButKeepSpacing() {
        Layout l = layout("plain **bold** end\n", 200);

        List<Element.Text> t = texts(l);
        assertEquals(List.of("plain", "bold", "end"), t.stream().map(Element.Text::text).toList());
        assertTrue(t.get(1).style().bold());
        assertEquals(6 * 6, t.get(1).x(), "'plain' (5) + one space = 6 characters");
        assertEquals(6 * 11, t.get(2).x());
    }

    @Test
    void headingsAreScaledAnchoredAndUnderlined() {
        Layout l = layout("para\n\n# Big\n\n### Small\n", 400);

        Element.Text big = texts(l).stream().filter(e -> e.text().equals("Big")).findFirst().orElseThrow();
        assertEquals(1.8f, big.scale());
        assertEquals(Element.TextRole.HEADING, big.role());
        assertTrue(big.style().bold());
        assertEquals(15, big.height(), "ceil(8 * 1.8)");

        assertTrue(l.anchors().containsKey("big"));
        assertEquals(big.y(), l.anchorY("big").orElseThrow());
        assertEquals(1, rects(l, Element.RectRole.HEADING_UNDERLINE).size(), "only h1/h2 are underlined");
    }

    @Test
    void layoutPageDrawsTheTitleFirst() {
        Document doc = MarkdownParser.parse("Body\n", "t.md", 1, new Diagnostics());
        Layout l = engine.layoutPage("My Page", doc, 200);

        assertEquals("My Page", texts(l).get(0).text());
        assertEquals(0, l.anchorY("top").orElseThrow());
        assertEquals("Body", texts(l).get(1).text());
    }

    @Test
    void linksCarryTheirDestinationAndAreHitTestable() {
        Layout l = layout("go [there now](other.md) ok\n", 200);

        Element.Text link = texts(l).stream().filter(t -> t.link() != null).findFirst().orElseThrow();
        assertEquals("there now", link.text());
        assertEquals("other.md", link.link());
        assertEquals(Element.TextRole.LINK, link.role());

        assertEquals(link, l.linkAt(link.x() + 1, link.y() + 1).orElseThrow());
        assertTrue(l.linkAt(0, 0).isEmpty(), "plain text is not a link");
        assertTrue(l.linkAt(1000, 1000).isEmpty());
    }

    @Test
    void inlineIconsOccupyIconSizeAndRaiseTheLine() {
        Layout l = layout("a ![Hopper](item:minecraft:hopper) b\n", 200);

        Element.Icon icon = l.elements().stream().filter(e -> e instanceof Element.Icon).map(e -> (Element.Icon) e).findFirst().orElseThrow();
        assertEquals("item", icon.kind());
        assertEquals("minecraft:hopper", icon.id());
        assertEquals(16, icon.width());
        assertEquals(16, l.height());
        assertEquals(0, icon.y());

        Element.Text a = texts(l).get(0);
        assertEquals(4, a.y(), "8px text is centred in the 16px line");
    }

    @Test
    void bulletListsIndentContentAndPlaceMarkers() {
        Layout l = layout("- one\n- two\n", 200);

        List<Element.Text> t = texts(l);
        Element.Text marker = t.stream().filter(e -> e.text().equals("•")).findFirst().orElseThrow();
        Element.Text one = t.stream().filter(e -> e.text().equals("one")).findFirst().orElseThrow();

        assertEquals(0, marker.x());
        assertEquals(6 + LayoutConfig.defaults().listGap(), one.x());
        assertEquals(2, t.stream().filter(e -> e.text().equals("•")).count());

        Element.Text two = t.stream().filter(e -> e.text().equals("two")).findFirst().orElseThrow();
        assertEquals(8 + LayoutConfig.defaults().listItemSpacing(), two.y());
    }

    @Test
    void orderedListNumbersAreRightAlignedAndNestedBulletsChangeGlyph() {
        Layout l = layout("9. nine\n10. ten\n\n- outer\n  - inner\n", 200);

        List<Element.Text> t = texts(l);
        Element.Text nine = t.stream().filter(e -> e.text().equals("9.")).findFirst().orElseThrow();
        Element.Text ten = t.stream().filter(e -> e.text().equals("10.")).findFirst().orElseThrow();
        assertEquals(6, nine.x(), "'9.' is right-aligned against the wider '10.'");
        assertEquals(0, ten.x());

        assertTrue(t.stream().anyMatch(e -> e.text().equals("-")), "second nesting level uses a dash");
    }

    @Test
    void taskItemsUseCheckboxMarkers() {
        Layout l = layout("- [x] done\n- [ ] todo\n", 200);

        List<String> texts = texts(l).stream().map(Element.Text::text).toList();
        assertTrue(texts.contains("[x]"));
        assertTrue(texts.contains("[ ]"));
    }

    @Test
    void quotesAreIndentedWithABar() {
        Layout l = layout("> quoted\n", 200);

        Element.Text q = texts(l).get(0);
        assertEquals(LayoutConfig.defaults().quoteIndent(), q.x());
        Element.Rect bar = rects(l, Element.RectRole.QUOTE_BAR).get(0);
        assertEquals(0, bar.x());
        assertEquals(8, bar.height());
    }

    @Test
    void calloutBackgroundIsDrawnBeforeItsText() {
        Layout l = layout("> [!NOTE] Heads up\n> Body text.\n", 200);

        List<Element> all = l.elements();
        int bg = -1;
        int firstText = -1;
        for (int i = 0; i < all.size(); i++) {
            if (bg < 0 && all.get(i) instanceof Element.Rect r && r.role() == Element.RectRole.CALLOUT_BACKGROUND) {
                bg = i;
            }
            if (firstText < 0 && all.get(i) instanceof Element.Text) {
                firstText = i;
            }
        }

        assertTrue(bg >= 0 && bg < firstText, "background must come first so it is not painted over the text");
        Element.Rect rect = (Element.Rect) all.get(bg);
        assertEquals("note", rect.variant());
        assertEquals(l.height(), rect.height());
        assertEquals("Heads up", texts(l).get(0).text());
        assertTrue(texts(l).get(0).style().bold() || texts(l).get(0).role() == Element.TextRole.CALLOUT_TITLE);
    }

    @Test
    void codeBlocksKeepLinesAndWrapByCharacter() {
        Layout l = layout("```\nshort\nabcdefghijklmnopqrstuvwxyz\n```\n", 12 * 6 + 8);

        List<String> lines = texts(l).stream().map(Element.Text::text).toList();
        assertEquals("short", lines.get(0));
        assertEquals("abcdefghijkl", lines.get(1));
        assertEquals("mnopqrstuvwx", lines.get(2));
        assertEquals("yz", lines.get(3));
        assertTrue(texts(l).stream().allMatch(t -> t.role() == Element.TextRole.CODE));
        assertEquals(1, rects(l, Element.RectRole.CODE_BACKGROUND).size());
    }

    @Test
    void ruleTakesAFewPixelsOfHeight() {
        Layout l = layout("a\n\n---\n\nb\n", 100);

        assertEquals(1, rects(l, Element.RectRole.RULE).size());
    }

    @Test
    void tablesPlaceColumnsLeftToRightWithBordersAndAHeaderFill() {
        Layout l = layout("| Name | Qty |\n|------|----:|\n| apple | 3 |\n", 300);

        Element.Text name = texts(l).stream().filter(t -> t.text().equals("Name")).findFirst().orElseThrow();
        Element.Text qty = texts(l).stream().filter(t -> t.text().equals("Qty")).findFirst().orElseThrow();
        Element.Text three = texts(l).stream().filter(t -> t.text().equals("3")).findFirst().orElseThrow();
        assertTrue(qty.x() > name.x());
        assertTrue(three.y() > name.y());

        assertEquals(1, rects(l, Element.RectRole.TABLE_HEADER).size());
        assertEquals(3, rects(l, Element.RectRole.TABLE_BORDER).stream().filter(r -> r.height() == 1 && r.width() > 1).count(), "top + under header + bottom");
        assertEquals(3, rects(l, Element.RectRole.TABLE_BORDER).stream().filter(r -> r.width() == 1).count(), "left + middle + right");

        // The right-aligned column hugs the right edge of its cell: "Qty" and "3" end at the same x.
        assertEquals(qty.x() + qty.width(), three.x() + three.width());
    }

    @Test
    void wideTablesShrinkToFitAndWrapCells() {
        Layout l = layout("| A | B |\n|---|---|\n| aaaa bbbb cccc dddd | eeee ffff gggg hhhh |\n", 90);

        int right = l.elements().stream().mapToInt(e -> e.x() + e.width()).max().orElseThrow();
        assertTrue(right <= 90, "table must stay inside the column, was " + right);
        assertTrue(texts(l).stream().filter(t -> t.text().contains("aaaa")).count() >= 1);
        assertTrue(l.height() > 3 * 8, "wrapped cells make rows taller");
    }

    @Test
    void narrowTablesWrapAtWordBoundariesInsteadOfSplittingWords() {
        // 130px: the natural width is far too wide, but every column can still hold its longest word.
        Layout l = layout("| Key | Used by | Meaning |\n|---|---|---|\n| recipe | all | Optional recipe id. See the list |\n", 130);

        List<String> texts = texts(l).stream().map(Element.Text::text).toList();
        assertTrue(texts.contains("Key"), texts.toString());
        assertTrue(texts.contains("recipe"), "a word must not be split: " + texts);
        assertTrue(texts.contains("Meaning"), texts.toString());
        assertTrue(texts.stream().anyMatch(t -> t.startsWith("Optional")), texts.toString());
        assertTrue(texts.stream().noneMatch(t -> t.length() == 1 && Character.isLetter(t.charAt(0))), "no one-letter fragments: " + texts);

        int right = l.elements().stream().mapToInt(e -> e.x() + e.width()).max().orElseThrow();
        assertTrue(right <= 130, "table must stay inside the column, was " + right);
    }

    @Test
    void recipeWidgetIsReservedCenteredSpace() {
        Layout l = layout("```recipe\npattern: [\"A\"]\nkey: { A: stone }\nresult: stick\n```\n", 200);

        Element.WidgetBox box = l.elements().stream().filter(e -> e instanceof Element.WidgetBox).map(e -> (Element.WidgetBox) e).findFirst().orElseThrow();
        assertEquals(132, box.width(), "the vanilla-style crafting panel");
        assertEquals(76, box.height());
        assertEquals(34, box.x(), "centred in 200px");
        assertEquals(76, l.height());
        assertEquals(box, l.interactiveAt(box.x() + 1, box.y() + 1).orElseThrow());
    }

    @Test
    void invalidWidgetShowsItsErrorAsText() {
        Layout l = layout("```recipe\npattern: [\"A\"]\nresult: stick\n```\n", 400);

        assertTrue(l.elements().stream().noneMatch(e -> e instanceof Element.WidgetBox));
        Element.Text msg = texts(l).get(0);
        assertEquals(Element.TextRole.ERROR, msg.role());
        assertTrue(msg.text().startsWith("[recipe:"));
    }

    @Test
    void narrowerColumnsNeverProduceAShorterLayout() {
        String md = "# Title\n\nSome reasonably long paragraph text that needs to wrap at least a couple of times.\n\n- item one\n- item two is a bit longer\n";

        int wide = layout(md, 300).height();
        int narrow = layout(md, 80).height();
        assertTrue(narrow > wide, narrow + " vs " + wide);
    }

    @Test
    void everyElementStaysInsideTheColumnForTypicalContent() {
        String md = """
            # Heading

            Paragraph with a [link](x.md) and **bold** and a looooooooooooooooooooooooong word.

            > [!TIP] Tip
            > Callout body with several words in it.

            1. one
            2. two

            ```
            code line that is long enough to wrap
            ```
            """;
        Layout l = layout(md, 120);

        for (Element e : l.elements()) {
            assertTrue(e.x() >= 0, e.toString());
            assertTrue(e.x() + e.width() <= 120, e.toString());
            assertTrue(e.y() >= 0 && e.y() + e.height() <= l.height() + 1, e.toString());
        }
        assertFalse(l.elements().isEmpty());
        assertInstanceOf(Element.Text.class, l.elements().stream().filter(e -> e instanceof Element.Text).findFirst().orElseThrow());
    }
}
