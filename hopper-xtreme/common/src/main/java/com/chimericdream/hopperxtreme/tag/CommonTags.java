package com.chimericdream.hopperxtreme.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CommonTags {
    public static final TagKey<Block> HOPPERS = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("c", "hoppers"));
    public static final TagKey<Item> WRENCHES = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("c", "tools/wrenches"));
}
