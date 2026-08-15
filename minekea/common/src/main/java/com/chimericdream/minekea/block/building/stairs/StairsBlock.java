package com.chimericdream.minekea.block.building.stairs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.StairBlock;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class StairsBlock extends StairBlock {
    public Identifier BLOCK_ID;
    public final BlockConfig config;

    public StairsBlock(BlockConfig config) {
        this(config, makeId(config.getMaterial()));
    }

    public StairsBlock(BlockConfig config, Identifier id) {
        super(config.getIngredient().defaultBlockState(), config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(id)));

        BLOCK_ID = id;
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/stairs/%s", material));
    }
}
