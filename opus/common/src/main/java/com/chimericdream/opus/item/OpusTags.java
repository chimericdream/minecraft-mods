package com.chimericdream.opus.item;

import com.chimericdream.opus.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OpusTags {
    /**
     * Items a Book of Books may hold besides Opus guides, which it always accepts. Empty by default; pack authors
     * add their own mods' guidebooks.
     */
    public static final TagKey<Item> BOOK_OF_BOOKS_STORABLE = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book_of_books_storable")
    );

    private OpusTags() {
    }
}
