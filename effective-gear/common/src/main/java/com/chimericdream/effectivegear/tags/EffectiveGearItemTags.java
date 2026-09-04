package com.chimericdream.effectivegear.tags;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EffectiveGearItemTags {
    public static final TagKey<Item> NETHERITE_HELMETS = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "netherite_helmets"));
    public static final TagKey<Item> NETHERITE_CHESTPLATES = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "netherite_chestplates"));
    public static final TagKey<Item> NETHERITE_LEGGINGS = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "netherite_leggings"));
    public static final TagKey<Item> NETHERITE_BOOTS = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "netherite_boots"));
}
