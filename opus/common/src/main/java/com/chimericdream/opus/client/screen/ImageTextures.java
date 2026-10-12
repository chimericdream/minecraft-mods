package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.ImageSizer;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Finds and draws the resource-pack textures that books use as images and chapter icons.
 *
 * <p>A book names a texture as {@code ns:path}, relative to {@code textures/} and without {@code .png}, the same
 * form as the {@code texture} key in {@code book.yml}. The layout needs the size before anything is drawn, so sizes
 * are read from the PNG header through the resource manager and cached until the next resource reload. Drawing goes
 * through the texture manager, which loads a texture the first time it is asked for it.
 */
public final class ImageTextures {
    private static final Map<String, Optional<ImageSizer.Size>> SIZES = new ConcurrentHashMap<>();

    private ImageTextures() {
    }

    /** The {@link ImageSizer} the layout uses; a texture that can't be read counts as missing. */
    public static ImageSizer sizer() {
        return ImageTextures::measure;
    }

    /** Forgets every cached size. Called on resource reload, since a new pack may change a texture. */
    public static void clear() {
        SIZES.clear();
    }

    /** The texture's size in pixels, or {@code null} when the id is malformed or no such texture exists. */
    public static ImageSizer.Size measure(String id) {
        return SIZES.computeIfAbsent(id, ImageTextures::read).orElse(null);
    }

    private static Optional<ImageSizer.Size> read(String id) {
        Identifier location = location(id);
        if (location == null) {
            return Optional.empty();
        }

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
            return Optional.of(new ImageSizer.Size(image.getWidth(), image.getHeight()));
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        }
    }

    /** {@code ns:path} becomes {@code ns:textures/path.png}; {@code null} when it isn't a valid identifier. */
    static Identifier location(String id) {
        int colon = id.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : id.substring(0, colon);
        String path = colon < 0 ? id : id.substring(colon + 1);
        if (namespace.isEmpty() || path.isEmpty() || path.contains("..")) {
            return null;
        }

        try {
            return Identifier.fromNamespaceAndPath(namespace, "textures/" + path + ".png");
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Draws the whole texture stretched to {@code w} x {@code h}. Does nothing when it can't be found. */
    static void draw(GuiGraphicsExtractor g, String id, int x, int y, int w, int h) {
        ImageSizer.Size size = measure(id);
        Identifier location = location(id);
        if (size == null || location == null) {
            return;
        }

        g.blit(RenderPipelines.GUI_TEXTURED, location, x, y, 0f, 0f, w, h, size.width(), size.height(), size.width(), size.height());
    }

    /** Draws the texture as large as fits in a {@code box} x {@code box} square, keeping its proportions, centred. */
    static void drawFitted(GuiGraphicsExtractor g, String id, int x, int y, int box) {
        ImageSizer.Size size = measure(id);
        if (size == null) {
            return;
        }

        float scale = Math.min(box / (float) size.width(), box / (float) size.height());
        int w = Math.max(1, Math.round(size.width() * scale));
        int h = Math.max(1, Math.round(size.height() * scale));
        draw(g, id, x + (box - w) / 2, y + (box - h) / 2, w, h);
    }
}
