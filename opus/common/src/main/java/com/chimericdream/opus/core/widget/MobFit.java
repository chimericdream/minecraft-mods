package com.chimericdream.opus.core.widget;

/**
 * Sizes the box for a live mob preview and works out how big to draw the mob inside it. Pure maths, so the layout
 * engine and the renderer get the same answer from the same inputs.
 *
 * <p>Every mob is drawn at the same scale, so mobs on one page keep their real proportions relative to each other.
 * That scale is anchored on the villager: at GUI scale {@value #REFERENCE_GUI_SCALE} a block is
 * {@value #REFERENCE_PIXELS_PER_BLOCK} GUI pixels, and other GUI scales keep the mob the same physical size on screen.
 * The widget's {@code scale} multiplies that.
 *
 * <p>The box starts at the villager's box and only grows (in steps of 16 px) when a bigger mob wouldn't fit. It
 * never exceeds the room the page has; a mob that is still too big for that is shrunk to fit.
 */
public final class MobFit {
    /** Box sides are multiples of this. */
    public static final int STEP = 16;
    /** Space between the mob and the box edge, on every side. */
    public static final int PAD = 4;

    public static final int REFERENCE_GUI_SCALE = 4;
    public static final int REFERENCE_PIXELS_PER_BLOCK = 64;

    private static final int MAX_BOX_HEIGHT = 512;
    private static final Bounds VILLAGER = new Bounds(0.6f, 1.95f);

    /** A mob's collision box, in blocks. */
    public record Bounds(float width, float height) {
    }

    /** {@code pixelsPerBlock} is the render scale to draw the mob at inside a {@code width} x {@code height} box. */
    public record Result(int width, int height, float pixelsPerBlock) {
    }

    private MobFit() {
    }

    public static Result fit(Bounds bounds, float scale, int availableWidth, int guiScale) {
        float pixelsPerBlock = (float) REFERENCE_PIXELS_PER_BLOCK * REFERENCE_GUI_SCALE / Math.max(1, guiScale) * scale;
        int maxWidth = Math.max(STEP, floorStep(availableWidth));

        int[] reference = boxFor(VILLAGER, pixelsPerBlock);
        int[] mob = boxFor(bounds, pixelsPerBlock);

        int boxWidth = Math.min(maxWidth, Math.max(reference[0], mob[0]));
        int boxHeight = Math.min(MAX_BOX_HEIGHT, Math.max(reference[1], mob[1]));

        float w = footprint(bounds)[0];
        float h = footprint(bounds)[1];
        // The box may have been clamped, so make sure the mob still fits.
        pixelsPerBlock = Math.min(pixelsPerBlock, Math.min((boxWidth - 2 * PAD) / w, (boxHeight - 2 * PAD) / h));

        return new Result(boxWidth, boxHeight, pixelsPerBlock);
    }

    /**
     * Room a mob needs: models stick out past the collision box (a villager's folded arms) and a turning mob sweeps
     * a circle, so width is padded, and a cubic mob's height is padded to match so its box stays square.
     */
    private static float[] footprint(Bounds bounds) {
        float widened = bounds.width() * 1.4f;
        float h = Math.max(Math.max(bounds.height(), widened), 0.1f);
        float w = Math.max(Math.max(widened, h * 0.6f), 0.1f);

        return new float[]{w, h};
    }

    private static int[] boxFor(Bounds bounds, float pixelsPerBlock) {
        float[] size = footprint(bounds);

        return new int[]{ceilStep(size[0] * pixelsPerBlock + 2 * PAD), ceilStep(size[1] * pixelsPerBlock + 2 * PAD)};
    }

    private static int ceilStep(float value) {
        return (int) Math.ceil(value / STEP) * STEP;
    }

    private static int floorStep(int value) {
        return value / STEP * STEP;
    }
}
