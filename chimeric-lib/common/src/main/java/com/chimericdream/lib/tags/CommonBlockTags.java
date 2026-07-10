package com.chimericdream.lib.tags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CommonBlockTags {
    public static final TagKey<Block> SHEARS_MINEABLE = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("c", "shears_mineable"));
}
