package com.chimericdream.minekea.block.building.general;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WarpedNetherBricksBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "building/general/warped_nether_bricks");

    public WarpedNetherBricksBlock() {
        super(AbstractBlock.Settings.copy(Blocks.RED_NETHER_BRICKS).registryKey(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }
}
