package com.chimericdream.minekea.fabric.util;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class RegistryUtil {
    public static RegistryEntryList<Item> makeRegistryEntryListForTag(TagKey<Item> tag) {
        List<RegistryEntry<Item>> items = new ArrayList<>();
        Registries.ITEM.iterateEntries(tag).forEach(items::add);

        return RegistryEntryList.of(items);
    }

    public static Ingredient makeTagIngredient(TagKey<Item> tag) {
        return Ingredient.ofTag(makeRegistryEntryListForTag(tag));
    }
}
