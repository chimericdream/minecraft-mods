package com.chimericdream.jdcrafte.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Mod-specific item tags. Currently just {@link #TRELLIS}, which groups every wood type's trellis
 * item (see {@code ModBlocks.TRELLIS_BLOCKS}) so recipes like the trellis arch's can accept "any
 * trellis" as an ingredient.
 */
public class ModItemTags {
    public static final TagKey<Item> TRELLIS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "trellis"));
}
