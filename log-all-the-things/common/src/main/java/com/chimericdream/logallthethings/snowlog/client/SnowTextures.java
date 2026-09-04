package com.chimericdream.logallthethings.snowlog.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * Resolves the texture a snow-logged straight stairs' hand-cut overlay box should use (see
 * {@link SnowStairsRenderer}). Unlike {@code carpetlog.client.CarpetFrameTextures} - which has to read
 * each carpet color's own model file, since the texture genuinely differs per carpet block - every one
 * of vanilla's {@code SnowLayerBlock.LAYERS} variants uses the exact same {@code block/snow} texture
 * regardless of layer count, and (unlike carpet colors) there's no single model file named after the
 * block itself to read it from - vanilla's snow models are named {@code snow_height2.json} etc., one
 * per layer count. Deriving the texture id directly from the placed block's own registry id (following
 * vanilla's own {@code <namespace>:block/<path>} naming convention) is simpler and still resource-pack
 * correct - a pack that reskins {@code block/snow} is picked up the same as it would be for the real
 * block's own rendering.
 */
public final class SnowTextures {
    private static final Map<Block, TextureAtlasSprite> CACHE = new HashMap<>();

    private SnowTextures() {
    }

    public static Optional<TextureAtlasSprite> get(Block snowBlock) {
        return Optional.ofNullable(CACHE.computeIfAbsent(snowBlock, SnowTextures::load));
    }

    private static TextureAtlasSprite load(Block snowBlock) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(snowBlock);
        Identifier texture = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());

        return Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, texture));
    }
}
