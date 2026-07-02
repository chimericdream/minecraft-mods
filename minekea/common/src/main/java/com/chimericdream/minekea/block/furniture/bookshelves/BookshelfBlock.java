package com.chimericdream.minekea.block.furniture.bookshelves;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BookshelfBlock extends Block {
    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public BookshelfBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/bookshelves/%s", material));
    }
}
