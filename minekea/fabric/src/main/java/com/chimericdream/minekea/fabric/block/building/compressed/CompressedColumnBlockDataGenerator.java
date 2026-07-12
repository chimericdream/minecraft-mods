package com.chimericdream.minekea.fabric.block.building.compressed;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.compressed.CompressedColumnBlock;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class CompressedColumnBlockDataGenerator extends CompressedBlockDataGenerator {
    public CompressedColumnBlockDataGenerator(Block block) {
        super(block);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.END, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_end", BLOCK.BLOCK_ID.getPath()))))
            .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_side", BLOCK.BLOCK_ID.getPath()))));

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", ModelTemplates.CUBE_COLUMN, unused -> textures);

        ModelUtils.registerBlockWithAxis(blockStateModelGenerator, CompressedColumnBlock.AXIS, BLOCK, subModelId);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().<Block>generate(Identifier.withDefaultNamespace("block"), instance -> {
            generateTexture(instance, BLOCK.config.getMaterial() + ((CompressedColumnBlock) BLOCK).endTextureSuffix, BLOCK.BLOCK_ID.withSuffix("_end"));
            generateTexture(instance, BLOCK.config.getMaterial() + ((CompressedColumnBlock) BLOCK).sideTextureSuffix, BLOCK.BLOCK_ID.withSuffix("_side"));
        });
    }
}
