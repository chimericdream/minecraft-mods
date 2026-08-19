package com.chimericdream.camelnostrils.tag;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> LIVNA_BLOCKS = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "livna_blocks"));
}
