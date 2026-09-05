package com.chimericdream.logallthethings.carpetlog.client;

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
 * Resolves the single texture a carpet block's own model already defines, so the carpet-overlay
 * geometry (see {@link CarpetFrameGeometry}) can be drawn in whatever color of carpet the player
 * actually used. Mirrors {@code windowlog.client.WindowFramePaneTextures}, minus the flat/edge split -
 * a carpet only ever shows the one texture.
 *
 * <p>Every vanilla carpet's model (e.g. {@code red_carpet.json}) is a thin wrapper around
 * {@code block/carpet.json} with a single texture slot named {@code wool}. Reading the carpet block's
 * own model file directly (rather than introspecting its baked model's quads) gets that texture
 * location generically, for any modded carpet that follows the same convention as vanilla's — no
 * hardcoded per-color texture paths needed. Falls back to the model's {@code particle} texture for
 * anything that doesn't define a {@code wool} slot.
 */
public final class CarpetFrameTextures {
    private static final Gson GSON = new Gson();
    private static final Map<Block, Optional<TextureAtlasSprite>> CACHE = new HashMap<>();

    private CarpetFrameTextures() {
    }

    public static Optional<TextureAtlasSprite> get(Block carpetBlock) {
        return CACHE.computeIfAbsent(carpetBlock, CarpetFrameTextures::load);
    }

    private static Optional<TextureAtlasSprite> load(Block carpetBlock) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(carpetBlock);
        Identifier modelLocation = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "models/block/" + blockId.getPath() + ".json");

        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(modelLocation);
            JsonObject json;
            try (var reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                json = GSON.fromJson(reader, JsonObject.class);
            }

            JsonObject textures = json.getAsJsonObject("textures");
            Identifier texture = readTexture(textures, "wool");
            if (texture == null) {
                texture = readTexture(textures, "particle");
            }
            if (texture == null) {
                return Optional.empty();
            }

            return Optional.of(sprite(texture));
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
