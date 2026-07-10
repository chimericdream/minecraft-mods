package com.chimericdream.shulkerstuff.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ShulkerStuffItemTags {
    public static final TagKey<Item> SHULKER_BOX_ITEMS = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.withDefaultNamespace("shulker_boxes"));
}
