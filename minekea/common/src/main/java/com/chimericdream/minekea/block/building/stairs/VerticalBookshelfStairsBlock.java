package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import net.minecraft.util.Identifier;

public class VerticalBookshelfStairsBlock extends VerticalStairsBlock {
    public final Identifier BASE_BLOCK_ID;

    public VerticalBookshelfStairsBlock(BlockConfig config) {
        super(config);

        BLOCK_ID = makeId(config.getMaterial());
        BASE_BLOCK_ID = BookshelfBlock.makeId(config.getMaterial());
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("building/stairs/bookshelves/vertical/%s", material));
    }
}
