package com.chimericdream.opus.core.book;

import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Inline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Small in-memory full-text index over a book. Every query word must match (as a prefix) a word in the page;
 * matches in the title or tags outrank matches in headings, which outrank body text.
 */
public final class SearchIndex {
    private static final Pattern SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");

    public record Hit(BookNode node, int score, String snippet) {
    }

    private record Entry(BookNode node, Set<String> title, Set<String> tags, Set<String> headings, Set<String> summary, Set<String> body, String bodyText) {
    }

    private final List<Entry> entries = new ArrayList<>();

    SearchIndex(List<BookNode> nodes) {
        for (BookNode node : nodes) {
            StringBuilder headings = new StringBuilder();
            for (Block.Heading h : node.document().headings()) {
                headings.append(Inline.plainText(h.content())).append(' ');
            }

            String bodyText = node.document().plainText();
            entries.add(new Entry(
                node,
                tokens(node.title() + " " + String.join(" ", node.aliases())),
                tokens(String.join(" ", node.tags())),
                tokens(headings.toString()),
                tokens(node.summary() == null ? "" : node.summary()),
                tokens(bodyText),
                bodyText
            ));
        }
    }

    public List<Hit> search(String query) {
        List<String> words = new ArrayList<>(tokens(query));
        if (words.isEmpty()) {
            return List.of();
        }

        List<Hit> hits = new ArrayList<>();
        for (Entry entry : entries) {
            int total = 0;
            boolean all = true;
            for (String word : words) {
                int best = Math.max(
                    Math.max(match(entry.title, word, 10), match(entry.tags, word, 6)),
                    Math.max(Math.max(match(entry.headings, word, 4), match(entry.summary, word, 3)), match(entry.body, word, 1))
                );
                if (best == 0) {
                    all = false;
                    break;
                }
                total += best;
            }

            if (all) {
                hits.add(new Hit(entry.node, total, snippet(entry.bodyText, words.get(0))));
            }
        }

        hits.sort(Comparator.comparingInt(Hit::score).reversed().thenComparing(h -> h.node().id()));
        return hits;
    }

    private static int match(Set<String> tokens, String word, int weight) {
        if (tokens.contains(word)) {
            return weight * 2;
        }
        for (String token : tokens) {
            if (token.startsWith(word)) {
                return weight;
            }
        }

        return 0;
    }

    private static Set<String> tokens(String text) {
        Set<String> out = new HashSet<>();
        for (String part : SPLIT.split(text.toLowerCase(Locale.ROOT))) {
            if (!part.isEmpty()) {
                out.add(part);
            }
        }

        return out;
    }

    private static String snippet(String text, String word) {
        int at = text.toLowerCase(Locale.ROOT).indexOf(word);
        if (at < 0) {
            return "";
        }

        int start = Math.max(0, at - 30);
        int end = Math.min(text.length(), at + word.length() + 50);
        return (start > 0 ? "..." : "") + text.substring(start, end).replace('\n', ' ').strip() + (end < text.length() ? "..." : "");
    }
}
