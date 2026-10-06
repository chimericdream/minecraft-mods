package com.chimericdream.opus.core.model;

import java.util.List;
import java.util.Map;

/** Block-level content of a page. */
public sealed interface Block {
    record Heading(int level, String anchor, List<Inline> content) implements Block {
    }

    record Paragraph(List<Inline> content) implements Block {
    }

    /** @param checked {@code null} for a normal item, otherwise the state of a {@code [ ]}/{@code [x]} task item */
    record ListItem(Boolean checked, List<Block> blocks) {
    }

    record ListBlock(boolean ordered, int start, List<ListItem> items) implements Block {
    }

    record Quote(List<Block> blocks) implements Block {
    }

    /** {@code > [!NOTE] Title} callout. {@code kind} is lower-case ({@code note}, {@code warning}, ...). */
    record Callout(String kind, String title, List<Block> blocks) implements Block {
    }

    record Code(String language, String text) implements Block {
    }

    record Rule() implements Block {
    }

    enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    record Table(List<Align> aligns, List<List<Inline>> header, List<List<List<Inline>>> rows) implements Block {
    }

    /**
     * A fenced block with a registered widget type ({@code recipe}, {@code item}, ...). {@code props} is the
     * parsed YAML body. {@code error} is non-null when the body could not be parsed or failed validation; the
     * renderer shows the message instead of the widget.
     */
    record Widget(String type, Map<String, Object> props, int line, String error) implements Block {
    }
}
