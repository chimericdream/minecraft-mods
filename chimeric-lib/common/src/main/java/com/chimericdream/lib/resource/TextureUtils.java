package com.chimericdream.lib.resource;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class TextureUtils {
    public static Identifier block(Block block) {
        return TextureUtils.block(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static Identifier block(Block block, String suffix) {
        return TextureUtils.block(BuiltInRegistries.BLOCK.getKey(block), suffix);
    }

    public static Identifier block(Identifier id) {
        return id.withPrefix("block/");
    }

    public static Identifier block(Identifier id, String suffix) {
        return id.withPrefix("block/").withSuffix(suffix);
    }
}
