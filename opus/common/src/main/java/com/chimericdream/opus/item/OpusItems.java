package com.chimericdream.opus.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/** Compiles against 26.2; not yet run in game. */
public class OpusItems {
    public static final RegistrySupplier<Item> BOOK = REGISTRY_HELPER.registerItem(OpusBookItem.ITEM_ID, OpusBookItem::new);

    public static final RegistrySupplier<Item> BOOK_OF_BOOKS = REGISTRY_HELPER.registerItem(BookOfBooksItem.ITEM_ID, BookOfBooksItem::new);

    public static void init() {
    }
}
