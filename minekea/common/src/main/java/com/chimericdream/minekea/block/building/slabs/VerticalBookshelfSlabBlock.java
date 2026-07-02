package com.chimericdream.minekea.block.building.slabs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import net.minecraft.resources.ResourceLocation;

public class VerticalBookshelfSlabBlock extends VerticalSlabBlock {
    public final ResourceLocation BASE_BLOCK_ID;

    public VerticalBookshelfSlabBlock(BlockConfig config) {
        super(config, makeId(config.getMaterial()));

        BLOCK_ID = makeId(config.getMaterial());
        BASE_BLOCK_ID = BookshelfBlock.makeId(config.getMaterial());
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/slabs/bookshelves/vertical/%s", material));
    }
}
