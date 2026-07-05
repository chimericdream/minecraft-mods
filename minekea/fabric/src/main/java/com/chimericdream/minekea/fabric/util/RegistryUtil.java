package com.chimericdream.minekea.fabric.util;


import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtil {
    public static HolderSet<Item> makeRegistryEntryListForTag(TagKey<Item> tag) {
        List<Holder<Item>> items = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(items::add);

        return HolderSet.direct(items);
    }

    public static Ingredient makeTagIngredient(TagKey<Item> tag) {
        return Ingredient.of(makeRegistryEntryListForTag(tag));
    }
}
