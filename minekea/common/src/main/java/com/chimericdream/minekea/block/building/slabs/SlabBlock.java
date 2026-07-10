package com.chimericdream.minekea.block.building.slabs;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class SlabBlock extends net.minecraft.world.level.block.SlabBlock {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public SlabBlock(BlockConfig config) {
        super(config.getBaseSettings().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial()))));

        BLOCK_ID = makeId(config.getMaterial());
        this.config = config;
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/slabs/%s", material));
    }
}
