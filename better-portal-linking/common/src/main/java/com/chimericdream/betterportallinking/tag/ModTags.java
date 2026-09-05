package com.chimericdream.betterportallinking.tag;

import com.chimericdream.betterportallinking.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> PORTAL_ADDRESS_BLOCKS = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "portal_address_blocks"));
}
