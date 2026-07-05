package com.chimericdream.minekea.fabric.block.building.compressed;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.compressed.CompressedColumnBlock;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CompressedColumnBlockDataGenerator extends CompressedBlockDataGenerator {
    public CompressedColumnBlockDataGenerator(Block block) {
        super(block);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.END, ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_end", BLOCK.BLOCK_ID.getPath())))
            .put(TextureSlot.SIDE, ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_side", BLOCK.BLOCK_ID.getPath())));

        ResourceLocation subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", ModelTemplates.CUBE_COLUMN, unused -> textures);

        ModelUtils.registerBlockWithAxis(blockStateModelGenerator, CompressedColumnBlock.AXIS, BLOCK, subModelId);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().<Block>generate(BuiltInRegistries.BLOCK.getDefaultKey(), instance -> {
            generateTexture(instance, BLOCK.config.getMaterial() + ((CompressedColumnBlock) BLOCK).endTextureSuffix, BLOCK.BLOCK_ID.withSuffix("_end"));
            generateTexture(instance, BLOCK.config.getMaterial() + ((CompressedColumnBlock) BLOCK).sideTextureSuffix, BLOCK.BLOCK_ID.withSuffix("_side"));
        });
    }
}
