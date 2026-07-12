package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.storage.DyeBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class DyeBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public final DyeBlock BLOCK;

    private static final ModelTemplate DYE_BLOCK_MODEL = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.TRANSLUCENT,
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/dye_block")),
        Optional.empty(),
        TextureSlot.BOTTOM,
        TextureSlot.SIDE,
        TextureSlot.TOP
    );

    public DyeBlockDataGenerator(Block block) {
        this.BLOCK = (DyeBlock) block;
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item dye = ColorHelpers.getDye(BLOCK.color);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', dye)
            .unlockedBy(RecipeProvider.getHasName(dye),
                generator.has(dye))
            .save(exporter);

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, dye, 9)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK),
                generator.has(BLOCK))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Compressed %s Dye", ColorHelpers.getName(BLOCK.color)));
        translationBuilder.add(BLOCK.asItem(), String.format("Compressed %s Dye", ColorHelpers.getName(BLOCK.color)));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/bottom", BLOCK.color))))
            .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/side", BLOCK.color))))
            .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/top", BLOCK.color))));

        blockStateModelGenerator.createTrivialBlock(
            BLOCK,
            TexturedModel.createDefault((unused) -> textures, DYE_BLOCK_MODEL)
        );
    }
}
