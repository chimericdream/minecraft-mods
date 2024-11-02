package com.chimericdream.lib.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CommonBlockTags {
    public static final TagKey<Block> SHEARS_MINEABLE = TagKey.of(Registries.BLOCK.getKey(), Identifier.of("c", "shears_mineable"));
}
