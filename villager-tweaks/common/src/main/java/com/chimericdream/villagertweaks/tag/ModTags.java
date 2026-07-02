package com.chimericdream.villagertweaks.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> IGNORED_ITEMS = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("villagertweaks", "ignored_trade_items"));
    public static final TagKey<Item> TEMPTATION_ITEMS = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("villagertweaks", "villager_temptation_items"));
}
