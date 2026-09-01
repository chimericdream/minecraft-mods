package com.chimericdream.logallthethings.windowlog.client;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;

/**
 * Resolves the two textures a window pane's own model already defines, so the glass frame geometry
 * (see {@link WindowFrameGeometry}) can be drawn in whatever color pane the player actually used.
 *
 * <p>Every vanilla pane's model (e.g. {@code red_stained_glass_pane_post.json}) is a thin wrapper
 * around {@code block/template_glass_pane_post.json} with two texture slots: {@code pane} (the flat
 * face — {@code #1} in this mod's frame models) and {@code edge} (the thin cut edge — {@code #2}).
 * Reading the pane block's own {@code <path>_post.json} directly (rather than introspecting its baked
 * model's quads) gets both concrete texture locations generically, for any modded pane that follows
 * the same convention as vanilla's — no hardcoded per-color texture paths needed.
 *
 * <p>Iron bars' own {@code iron_bars_post.json} follows the same {@code template_..._post.json} +
 * {@code edge} shape but names its flat-face slot {@code bars} instead of {@code pane} (both slots
 * point at the same texture there, since a bars grate looks the same from the flat and edge sides) —
 * {@link #load} tries {@code pane} first and falls back to {@code bars} so both window types resolve
 * through the one lookup.
 *
 * <p>A waxed copper bars variant (e.g. {@code waxed_weathered_copper_bars}) has no {@code _post.json}
 * of its own — its blockstate points straight at its unwaxed counterpart's model
 * ({@code weathered_copper_bars_post.json}), since waxing only stops further oxidation and doesn't
 * change appearance. {@link #load} retries with a stripped {@code waxed_} prefix on a lookup miss so
 * those still resolve instead of falling back to flat-pane rendering.
 */
public final class WindowFramePaneTextures {
    public record PaneSprites(TextureAtlasSprite flat, TextureAtlasSprite edge) {
    }

    private static final Gson GSON = new Gson();
    private static final Map<Block, Optional<PaneSprites>> CACHE = new HashMap<>();

    private WindowFramePaneTextures() {
    }

    public static Optional<PaneSprites> get(Block paneBlock) {
        return CACHE.computeIfAbsent(paneBlock, WindowFramePaneTextures::load);
    }

    private static Optional<PaneSprites> load(Block paneBlock) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(paneBlock);

        Optional<PaneSprites> sprites = loadModel(blockId.getNamespace(), blockId.getPath());
        if (sprites.isPresent() || !blockId.getPath().startsWith("waxed_")) {
            return sprites;
        }

        return loadModel(blockId.getNamespace(), blockId.getPath().substring("waxed_".length()));
    }

    private static Optional<PaneSprites> loadModel(String namespace, String path) {
        Identifier modelLocation = Identifier.fromNamespaceAndPath(namespace, "models/block/" + path + "_post.json");

        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(modelLocation);
            JsonObject json;
            try (var reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                json = GSON.fromJson(reader, JsonObject.class);
            }

            JsonObject textures = json.getAsJsonObject("textures");
            Identifier flat = readTexture(textures, "pane");
            if (flat == null) {
                flat = readTexture(textures, "bars");
            }
            Identifier edge = readTexture(textures, "edge");
            if (flat == null || edge == null) {
                return Optional.empty();
            }

            return Optional.of(new PaneSprites(sprite(flat), sprite(edge)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** A texture slot value is either a plain string path or {@code {"sprite": "...", ...}}. */
    private static Identifier readTexture(JsonObject textures, String key) {
        if (textures == null || !textures.has(key)) {
            return null;
        }

        var value = textures.get(key);
        String path = value.isJsonObject() ? value.getAsJsonObject().get("sprite").getAsString() : value.getAsString();

        return Identifier.parse(path);
    }

    private static TextureAtlasSprite sprite(Identifier texture) {
        return Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, texture));
    }
}
