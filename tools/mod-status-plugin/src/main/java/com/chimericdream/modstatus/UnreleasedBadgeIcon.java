package com.chimericdream.modstatus;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A small amber dot, drawn rather than shipped as an SVG so it scales with the IDE's DPI setting and
 * needs no light/dark asset pair.
 */
final class UnreleasedBadgeIcon implements Icon {
    static final UnreleasedBadgeIcon INSTANCE = new UnreleasedBadgeIcon();

    /** Amber in both themes -- brighter on dark backgrounds so it stays legible. */
    static final JBColor COLOR = new JBColor(0xB8730A, 0xE8A33D);

    private static final int UNSCALED_SIZE = 6;

    private UnreleasedBadgeIcon() {
    }

    @Override
    public void paintIcon(Component component, @NotNull Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR);
            g2.fillOval(x, y, getIconWidth(), getIconHeight());
        } finally {
            g2.dispose();
        }
    }

    @Override
    public int getIconWidth() {
        return JBUI.scale(UNSCALED_SIZE);
    }

    @Override
    public int getIconHeight() {
        return JBUI.scale(UNSCALED_SIZE);
    }
}
