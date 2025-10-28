package com.chimericdream.houdiniblock.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import static com.chimericdream.houdiniblock.HoudiniBlockMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> HOUDINI_BLOCK = REGISTRY_HELPER.registerBlock(
        "houdini_block",
        () -> new HoudiniBlock(
            AbstractBlock.Settings
                .copy(Blocks.STONE)
                .sounds(BlockSoundGroup.STONE)
                .breakInstantly()
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, REGISTRY_HELPER.makeId("houdini_block")))
        )
    );

    public static void init() {
    }
}
