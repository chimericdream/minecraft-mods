package com.chimericdream.minekea.tag;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MinekeaItemTags {
    public static final TagKey<Item> BAGGED_ITEMS = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "bagged_items"));
    public static final TagKey<Item> GLASS_JAR_STORABLE = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "glass_jar_storable"));
    public static final TagKey<Item> VOTIVE_CANDLES = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "votive_candles"));
}
