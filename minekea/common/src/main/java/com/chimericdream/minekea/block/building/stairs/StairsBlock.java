package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class StairsBlock extends net.minecraft.world.level.block.StairBlock {
    public ResourceLocation BLOCK_ID;
    public final BlockConfig config;

    public StairsBlock(BlockConfig config) {
        super(config.getIngredient().defaultBlockState(), config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/%s", material));
    }
}
