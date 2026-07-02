package com.chimericdream.minekea.block.building.general;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BasaltBricksBlock extends Block {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "building/general/basalt_bricks");

    public BasaltBricksBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_BASALT).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }
}
