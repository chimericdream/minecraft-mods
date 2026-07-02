package com.chimericdream.houdiniblock.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.houdiniblock.HoudiniBlockMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> HOUDINI_BLOCK = REGISTRY_HELPER.registerBlock(
        "houdini_block",
        () -> new HoudiniBlock(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.STONE)
                .sound(SoundType.STONE)
                .instabreak()
                .setId(ResourceKey.create(Registries.BLOCK, REGISTRY_HELPER.makeId("houdini_block")))
        )
    );

    public static void init() {
    }
}
