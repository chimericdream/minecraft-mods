package com.chimericdream.lib.resource;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TextureUtils {
    public static ResourceLocation block(Block block) {
        return TextureUtils.block(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static ResourceLocation block(Block block, String suffix) {
        return TextureUtils.block(BuiltInRegistries.BLOCK.getKey(block), suffix);
    }

    public static ResourceLocation block(ResourceLocation id) {
        return id.withPrefix("block/");
    }

    public static ResourceLocation block(ResourceLocation id, String suffix) {
        return id.withPrefix("block/").withSuffix(suffix);
    }
}
