package com.chimericdream.opus.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/** UNVERIFIED: never compiled. */
public class OpusItems {
    public static final RegistrySupplier<Item> BOOK = REGISTRY_HELPER.registerItem(OpusBookItem.ITEM_ID, OpusBookItem::new);

    public static void init() {
    }
}
