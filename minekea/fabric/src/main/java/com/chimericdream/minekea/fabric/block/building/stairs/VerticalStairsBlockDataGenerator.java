package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.VerticalStairsBlock;
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

public class VerticalStairsBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final Model VERTICAL_STAIRS_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/building/stairs/vertical")),
        Optional.empty(),
        TextureKey.ALL
    );

    public final VerticalStairsBlock BLOCK;

    public VerticalStairsBlockDataGenerator(Block block) {
        BLOCK = (VerticalStairsBlock) block;
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

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("###")
            .pattern(" ##")
            .pattern("  #")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Vertical %s Stairs", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Vertical %s Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap().put(TextureKey.ALL, BLOCK.config.getTexture());

        ModelUtils.registerVerticalStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            VERTICAL_STAIRS_MODEL
        );
    }
}
