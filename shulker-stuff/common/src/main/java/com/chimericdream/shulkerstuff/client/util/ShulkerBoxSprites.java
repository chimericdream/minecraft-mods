package com.chimericdream.shulkerstuff.client.util;

import com.chimericdream.shulkerstuff.ModInfo;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import static net.minecraft.client.render.TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE;

public class ShulkerBoxSprites {
    public static final SpriteIdentifier GRAYSCALE_SHULKER = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, Identifier.of(ModInfo.MOD_ID, "entity/shulker/grayscale"));
    public static final SpriteIdentifier GRAYSCALE_SHULKER_BOX = new SpriteIdentifier(Identifier.ofVanilla("textures/atlas/blocks.png"), Identifier.of(ModInfo.MOD_ID, "block/shulker_grayscale"));
}
