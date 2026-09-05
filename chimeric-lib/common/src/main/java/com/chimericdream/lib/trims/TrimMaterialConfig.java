package com.chimericdream.lib.trims;

/**
 * Declares one custom {@link net.minecraft.world.item.equipment.trim.TrimMaterial}: the registry id,
 * the color used for its description text, and the already-hand-drawn palette-permutation texture
 * that recolors vanilla's trim overlays for this material.
 *
 * @param namespace          the owning mod's id, used for the registry/translation keys.
 * @param id                 the material's path, e.g. {@code "ender_pearl"}.
 * @param color              ARGB/RGB color (as used by {@code Style.withColor(int)}) for the
 *                           material's description text.
 * @param paletteTexturePath path (relative to {@code assets/<namespace>/textures/}) to the
 *                           paletted-permutation texture for this material, e.g.
 *                           {@code "trims/color_palettes/ender_pearl"}.
 * @param displayName        the English display name, e.g. {@code "Ender Pearl Material"} — fed
 *                           into the mod's own lang provider under {@link #translationKey()}.
 */
public record TrimMaterialConfig(String namespace, String id, int color, String paletteTexturePath, String displayName) {
    /** The {@code asset_name} used in the generated {@code trim_material} JSON. Defaults to {@link #id}. */
    public String assetName() {
        return id;
    }

    /** The lang key for this material's display name: {@code trim_material.<namespace>.<id>}. */
    public String translationKey() {
        return "trim_material." + namespace + "." + id;
    }

    /** The atlas {@code permutations} key, and the full identifier of the palette texture it maps to. */
    public String paletteTextureId() {
        return namespace + ":" + paletteTexturePath;
    }
}
