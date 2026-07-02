package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import net.minecraft.resources.ResourceLocation;

public class BookshelfStairsBlock extends StairsBlock {
    public final ResourceLocation BASE_BLOCK_ID;

    public BookshelfStairsBlock(BlockConfig config) {
        super(config);

        BLOCK_ID = makeId(config.getMaterial());
        BASE_BLOCK_ID = BookshelfBlock.makeId(config.getMaterial());
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/bookshelves/%s", material));
    }
}
