package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.frontmatter.Frontmatter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrontmatterTest {
    private static Frontmatter parse(String text, Diagnostics d) {
        return Frontmatter.parse(text, d, "test.md");
    }

    @Test
    void parsesKeysAndBody() {
        Diagnostics d = new Diagnostics();
        Frontmatter fm = parse("---\ntitle: Hoppers\norder: 3\nhidden: true\ntags: [a, b]\n---\n# Body\n", d);

        assertEquals("Hoppers", fm.string("title"));
        assertEquals(3.0, fm.number("order"));
        assertTrue(fm.bool("hidden", false));
        assertEquals(List.of("a", "b"), fm.stringList("tags"));
        assertEquals("# Body\n", fm.body());
        assertEquals(7, fm.bodyStartLine());
        assertTrue(d.all().isEmpty());
    }

    @Test
    void noFrontmatterMeansWholeFileIsBody() {
        Frontmatter fm = parse("# Just text\n", new Diagnostics());

        assertTrue(fm.data().isEmpty());
        assertEquals("# Just text\n", fm.body());
        assertEquals(1, fm.bodyStartLine());
    }

    @Test
    void horizontalRuleMidFileIsNotFrontmatter() {
        Frontmatter fm = parse("Intro\n\n---\n\nMore\n", new Diagnostics());

        assertTrue(fm.data().isEmpty());
        assertTrue(fm.body().startsWith("Intro"));
    }

    @Test
    void tagsAcceptCommaSeparatedString() {
        Frontmatter fm = parse("---\ntags: one, two ,three\n---\n", new Diagnostics());

        assertEquals(List.of("one", "two", "three"), fm.stringList("tags"));
    }

    @Test
    void handlesWindowsLineEndingsAndBom() {
        Frontmatter fm = parse("﻿---\r\ntitle: Win\r\n---\r\nBody\r\n", new Diagnostics());

        assertEquals("Win", fm.string("title"));
        assertEquals("Body\n", fm.body());
    }

    @Test
    void unterminatedFrontmatterWarnsAndKeepsContent() {
        Diagnostics d = new Diagnostics();
        Frontmatter fm = parse("---\ntitle: Oops\nno closing delimiter\n", d);

        assertTrue(fm.data().isEmpty());
        assertEquals(1, d.warningCount());
        assertTrue(fm.body().contains("no closing delimiter"));
    }

    @Test
    void invalidYamlIsReportedWithAFileLine() {
        Diagnostics d = new Diagnostics();
        Frontmatter fm = parse("---\ntitle: ok\ntags: [unclosed\n---\nBody\n", d);

        assertTrue(d.hasErrors());
        assertTrue(d.all().get(0).line() >= 3, "line should point into the frontmatter: " + d.all().get(0));
        assertEquals("Body\n", fm.body());
    }

    @Test
    void missingKeysAreNullOrFallback() {
        Frontmatter fm = parse("---\ntitle: x\n---\n", new Diagnostics());

        assertNull(fm.string("icon"));
        assertNull(fm.number("order"));
        assertFalse(fm.bool("hidden", false));
        assertTrue(fm.stringList("tags").isEmpty());
    }
}
