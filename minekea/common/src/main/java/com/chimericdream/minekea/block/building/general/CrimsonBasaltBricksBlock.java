package com.chimericdream.minekea.block.building.general;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class CrimsonBasaltBricksBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "building/general/crimson_basalt_bricks");

    public CrimsonBasaltBricksBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_BASALT).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }
}
