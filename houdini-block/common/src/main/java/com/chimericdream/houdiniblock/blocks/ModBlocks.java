package com.chimericdream.houdiniblock.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BlockSoundGroup;

import static com.chimericdream.houdiniblock.registry.ModRegistries.registerBlock;

public class ModBlocks {
    public static final RegistrySupplier<Block> HOUDINI_BLOCK = registerBlock(
        "houdini_block",
        () -> new HoudiniBlock(AbstractBlock.Settings.copy(Blocks.STONE).sounds(BlockSoundGroup.STONE).breakInstantly())
    );

    public static void init() {
    }
}
