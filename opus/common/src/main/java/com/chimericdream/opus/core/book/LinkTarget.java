package com.chimericdream.opus.core.book;

/** What a Markdown link destination points at, once resolved against a book. */
public sealed interface LinkTarget {
    /** {@code http(s):} or {@code mailto:}. */
    record External(String url) implements LinkTarget {
    }

    /** A page (or chapter) of the same book. {@code anchor} may be {@code null}. */
    record Page(BookNode node, String anchor) implements LinkTarget {
    }

    /** A page in another book. Not validated at load time; the book may not be installed. */
    record OtherBook(String bookId, String pagePath, String anchor) implements LinkTarget {
    }

    /** {@code item:}, {@code block:}, {@code entity:}, {@code fluid:} or {@code tag:} - a game object. */
    record Reference(String kind, String id) implements LinkTarget {
    }

    record Broken(String reason) implements LinkTarget {
    }
}
