package com.chimericdream.houdiniblock.items;

import com.chimericdream.houdiniblock.blocks.ModBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.chimericdream.houdiniblock.HoudiniBlockMod.REGISTRY_HELPER;

public class ModItems {
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

    public static final RegistrySupplier<Item> HOUDINI_BLOCK_ITEM = REGISTRY_HELPER.registerItem(
        "houdini_block",
        () -> new HoudiniBlockItem(ModBlocks.HOUDINI_BLOCK.get(), DEFAULT_SETTINGS)
    );

    public static void init() {
    }
}
