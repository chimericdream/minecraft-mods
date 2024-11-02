package com.chimericdream.lib.registries;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryHelpers {
    public static void registerBlock(Block block, Identifier blockId) {
        Registry.register(Registries.BLOCK, blockId, block);
    }

    public static void registerBlockWithItem(Block block, Identifier blockId) {
        registerBlock(block, blockId);
        Registry.register(Registries.ITEM, blockId, new BlockItem(block, new Item.Settings()));
    }
}
