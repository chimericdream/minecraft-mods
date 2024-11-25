package com.chimericdream.shulkerstuff.tag;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ShulkerStuffItemTags {
    public static final TagKey<Item> SHULKER_BOX_ITEMS = TagKey.of(Registries.ITEM.getKey(), Identifier.ofVanilla("shulker_boxes"));
}
