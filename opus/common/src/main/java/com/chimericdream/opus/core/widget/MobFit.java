package com.chimericdream.opus.core.widget;

/**
 * Sizes the box for a live mob preview and works out how big to draw the mob inside it. Pure maths, so the layout
 * engine and the renderer get the same answer from the same inputs.
 *
 * <p>The mob is first fitted to a comfortable maximum area and then multiplied by the widget's {@code scale}. The box
 * is that size plus padding on all four sides, rounded up to a multiple of 16 px, so tall mobs get tall boxes and
 * roughly cubic mobs get square ones. The mob itself is not snapped to anything.
 */
public final class MobFit {
    /** Box sides are multiples of this. */
    public static final int STEP = 16;
    /** Space between the mob and the box edge, on every side. */
    public static final int PAD = 4;

    static final int MIN_BOX = 32;
    static final int MAX_BOX = 192;
    private static final int MAX_INNER_WIDTH = 80;
    private static final int MAX_INNER_HEIGHT = 112;

    /** A mob's collision box, in blocks. */
    public record Bounds(float width, float height) {
    }

    /** {@code pixelsPerBlock} is the render scale to draw the mob at inside a {@code width} x {@code height} box. */
    public record Result(int width, int height, float pixelsPerBlock) {
    }

    private MobFit() {
    }

    public static Result fit(Bounds bounds, float scale, int availableWidth) {
        float w = Math.max(bounds.width(), 0.1f);
        float h = Math.max(bounds.height(), 0.1f);

        float pixelsPerBlock = Math.min(MAX_INNER_WIDTH / w, MAX_INNER_HEIGHT / h) * scale;

        int maxWidth = Math.max(STEP, Math.min(MAX_BOX, floorStep(availableWidth)));
        int minWidth = Math.min(MIN_BOX, maxWidth);

        int boxWidth = clamp(ceilStep(w * pixelsPerBlock + 2 * PAD), minWidth, maxWidth);
        int boxHeight = clamp(ceilStep(h * pixelsPerBlock + 2 * PAD), MIN_BOX, MAX_BOX);

        // The box may have been clamped, so make sure the mob still fits.
        pixelsPerBlock = Math.min(pixelsPerBlock, Math.min((boxWidth - 2 * PAD) / w, (boxHeight - 2 * PAD) / h));

        return new Result(boxWidth, boxHeight, pixelsPerBlock);
    }

    private static int ceilStep(float value) {
        return (int) Math.ceil(value / STEP) * STEP;
    }

    private static int floorStep(int value) {
        return value / STEP * STEP;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
