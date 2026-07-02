package com.chimericdream.houdiniblock.items;

import com.chimericdream.houdiniblock.blocks.ModBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.houdiniblock.HoudiniBlockMod.REGISTRY_HELPER;

public class ModItems {
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    public static final RegistrySupplier<Item> HOUDINI_BLOCK_ITEM = REGISTRY_HELPER.registerItem(
        "houdini_block",
        () -> new HoudiniBlockItem(
            ModBlocks.HOUDINI_BLOCK.get(),
            DEFAULT_SETTINGS.setId(ResourceKey.create(Registries.ITEM, REGISTRY_HELPER.makeId("houdini_block")))
        )
    );

    public static void init() {
    }
}
