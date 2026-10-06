package com.chimericdream.opus.core.layout;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The result of laying a page out at a given width. Re-layout whenever the width changes; the result is
 * immutable and cheap to scroll.
 *
 * @param height  total content height in pixels
 * @param anchors heading anchor to its y position, for jump-to-section
 */
public record Layout(List<Element> elements, int height, Map<String, Integer> anchors) {
    /** The link under the point, if any (topmost wins). */
    public Optional<Element> linkAt(int px, int py) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            Element e = elements.get(i);
            if (e.contains(px, py)) {
                if (e instanceof Element.Text t && t.link() != null) {
                    return Optional.of(e);
                }
                if (e instanceof Element.Icon icon && icon.link() != null) {
                    return Optional.of(e);
                }
            }
        }

        return Optional.empty();
    }

    /** The interactive element under the point: a link, an icon or a widget (for tooltips). */
    public Optional<Element> interactiveAt(int px, int py) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            Element e = elements.get(i);
            if (!e.contains(px, py)) {
                continue;
            }
            if (e instanceof Element.Icon || e instanceof Element.WidgetBox) {
                return Optional.of(e);
            }
            if (e instanceof Element.Text t && t.link() != null) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }

    public Optional<Integer> anchorY(String anchor) {
        return Optional.ofNullable(anchors.get(anchor));
    }
}
