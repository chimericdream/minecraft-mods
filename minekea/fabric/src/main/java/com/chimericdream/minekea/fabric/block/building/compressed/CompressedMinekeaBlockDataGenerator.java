package com.chimericdream.minekea.fabric.block.building.compressed;

import com.chimericdream.minekea.block.building.compressed.CompressedMinekeaBlock;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class CompressedMinekeaBlockDataGenerator extends CompressedBlockDataGenerator {
    public CompressedMinekeaBlockDataGenerator(Block block) {
        super(block);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().<Block>generate(ResourceLocation.withDefaultNamespace("block"), instance -> {
            final Optional<BufferedImage> source = instance.getMinekeaImage(((CompressedMinekeaBlock) BLOCK).baseBlockId.withPrefix("block/").getPath());
            addTextureOverlay(instance, source, BLOCK.BLOCK_ID);
        });
    }
}
