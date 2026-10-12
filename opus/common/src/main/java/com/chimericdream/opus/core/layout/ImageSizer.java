package com.chimericdream.opus.core.layout;

/**
 * Looks up the pixel size of a resource-pack texture so the layout can reserve room for it before it is drawn.
 */
@FunctionalInterface
public interface ImageSizer {
    record Size(int width, int height) {
    }

    /** Sizes nothing: every texture counts as missing. */
    ImageSizer NONE = id -> null;

    /**
     * @param id a texture id such as {@code mymod:gui/diagram} (relative to {@code textures/}, no extension)
     * @return the texture's size, or {@code null} when it can't be found or read
     */
    Size measure(String id);
}
