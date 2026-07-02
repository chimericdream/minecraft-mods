package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import net.minecraft.resources.ResourceLocation;

public class VerticalBookshelfStairsBlock extends VerticalStairsBlock {
    public final ResourceLocation BASE_BLOCK_ID;

    public VerticalBookshelfStairsBlock(BlockConfig config) {
        super(config, makeId(config.getMaterial()));

        BLOCK_ID = makeId(config.getMaterial());
        BASE_BLOCK_ID = BookshelfBlock.makeId(config.getMaterial());
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/bookshelves/vertical/%s", material));
    }
}
