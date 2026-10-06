package com.chimericdream.opus.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** The parsed body of one page. */
public record Document(List<Block> blocks) {
    public static final Document EMPTY = new Document(List.of());

    public List<Block.Heading> headings() {
        List<Block.Heading> out = new ArrayList<>();
        walk(blocks, b -> {
            if (b instanceof Block.Heading h) {
                out.add(h);
            }
        });
        return out;
    }

    public boolean hasAnchor(String anchor) {
        return headings().stream().anyMatch(h -> anchor.equals(h.anchor()));
    }

    public String plainText() {
        StringBuilder sb = new StringBuilder();
        walk(blocks, b -> {
            switch (b) {
                case Block.Heading h -> sb.append(Inline.plainText(h.content())).append('\n');
                case Block.Paragraph p -> sb.append(Inline.plainText(p.content())).append('\n');
                case Block.Table t -> {
                    t.header().forEach(c -> sb.append(Inline.plainText(c)).append(' '));
                    t.rows().forEach(r -> r.forEach(c -> sb.append(Inline.plainText(c)).append(' ')));
                    sb.append('\n');
                }
                case Block.Callout c -> sb.append(c.title()).append('\n');
                default -> {
                }
            }
        });
        return sb.toString();
    }

    /** Visits every inline in the document, including those in lists, quotes, callouts and tables. */
    public void forEachInline(Consumer<Inline> visitor) {
        walk(blocks, b -> {
            switch (b) {
                case Block.Heading h -> visitInlines(h.content(), visitor);
                case Block.Paragraph p -> visitInlines(p.content(), visitor);
                case Block.Table t -> {
                    t.header().forEach(c -> visitInlines(c, visitor));
                    t.rows().forEach(r -> r.forEach(c -> visitInlines(c, visitor)));
                }
                default -> {
                }
            }
        });
    }

    private static void visitInlines(List<Inline> inlines, Consumer<Inline> visitor) {
        for (Inline inline : inlines) {
            visitor.accept(inline);
            if (inline instanceof Inline.Link l) {
                visitInlines(l.children(), visitor);
            }
        }
    }

    /** Depth-first walk over every block, descending into lists, quotes and callouts. */
    public static void walk(List<Block> blocks, Consumer<Block> visitor) {
        for (Block block : blocks) {
            visitor.accept(block);
            switch (block) {
                case Block.ListBlock l -> l.items().forEach(i -> walk(i.blocks(), visitor));
                case Block.Quote q -> walk(q.blocks(), visitor);
                case Block.Callout c -> walk(c.blocks(), visitor);
                default -> {
                }
            }
        }
    }
}
