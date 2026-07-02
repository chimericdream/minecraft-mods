package com.chimericdream.minekea.block.furniture.trapdoors;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BookshelfTrapdoorBlock extends TrapDoorBlock {
    public final ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public BookshelfTrapdoorBlock(BlockSetType type, BlockConfig config) {
        super(type, config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/trapdoors/bookshelves/%s", material));
    }
}
