package com.chimericdream.minekea.block.furniture.doors;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BookshelfDoorBlock extends DoorBlock {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public BookshelfDoorBlock(BlockSetType type, BlockConfig config) {
        super(type, BlockBehaviour.Properties.ofFullCopy(config.getIngredient()).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/doors/bookshelves/%s", material));
    }
}
