package com.chimericdream.minekea.block.decorations.lighting;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class LanternBlock extends net.minecraft.world.level.block.LanternBlock {
    public final Identifier BLOCK_ID;
    public final BlockConfig config;

    public LanternBlock(BlockConfig config, String name) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).noOcclusion().setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(name))));

        this.BLOCK_ID = makeId(name);
        this.config = config;
    }

    public static Identifier makeId(String name) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("decorations/lighting/%s_lantern", name));
    }
}
