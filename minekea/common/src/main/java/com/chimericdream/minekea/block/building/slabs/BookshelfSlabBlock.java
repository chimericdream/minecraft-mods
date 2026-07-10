package com.chimericdream.minekea.block.building.slabs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SlabBlock;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BookshelfSlabBlock extends SlabBlock {
    public final Identifier BLOCK_ID;
    public final Identifier BASE_BLOCK_ID;
    public final BlockConfig config;

    public BookshelfSlabBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        BASE_BLOCK_ID = BookshelfBlock.makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/slabs/bookshelves/%s", material));
    }
}
