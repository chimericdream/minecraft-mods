package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.covers.CoverBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

import static com.chimericdream.minekea.block.building.covers.CoverBlock.FACING;

public class CoverBlockDataGenerator extends ChimericLibBlockDataGenerator {
    // yowza
    public static final Model COVER_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/building/cover")),
        Optional.empty(),
        TextureKey.END,
        TextureKey.SIDE
    );

    public CoverBlock BLOCK;

    public CoverBlockDataGenerator(Block block) {
        BLOCK = (CoverBlock) block;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 16)
            .pattern("# #")
            .pattern("   ")
            .pattern("# #")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Cover", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier endTextureId = BLOCK.config.getTexture();
        Identifier sideTextureId = Optional.ofNullable(BLOCK.config.getTexture("side")).orElse(endTextureId);

        TextureMap textures = new TextureMap()
            .put(TextureKey.END, endTextureId)
            .put(TextureKey.SIDE, sideTextureId);

        Identifier subModelId = blockStateModelGenerator.createSubModel(BLOCK, "", COVER_MODEL, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, FACING, BLOCK, subModelId);
    }
}
