package com.chimericdream.minekea.block.building.walls;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WallBlock extends net.minecraft.world.level.block.WallBlock {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public WallBlock(BlockConfig config) {
        super(BlockBehaviour.Properties.ofFullCopy(config.getIngredient()).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/walls/%s", material));
    }
}
