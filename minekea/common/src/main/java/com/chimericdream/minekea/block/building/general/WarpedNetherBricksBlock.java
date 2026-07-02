package com.chimericdream.minekea.block.building.general;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WarpedNetherBricksBlock extends Block {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "building/general/warped_nether_bricks");

    public WarpedNetherBricksBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }
}
