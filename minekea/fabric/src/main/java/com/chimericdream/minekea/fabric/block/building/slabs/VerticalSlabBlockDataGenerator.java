package com.chimericdream.minekea.fabric.block.building.slabs;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.slabs.VerticalSlabBlock;
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

public class VerticalSlabBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final Model VERTICAL_SLAB_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/building/slabs/vertical")),
        Optional.empty(),
        TextureKey.ALL
    );

    private final VerticalSlabBlock BLOCK;

    public VerticalSlabBlockDataGenerator(Block block) {
        this.BLOCK = (VerticalSlabBlock) block;
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

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("#")
            .pattern("#")
            .pattern("#")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Vertical %s Slab", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Vertical %s Slab", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier textureId = BLOCK.config.getTexture();

        TextureMap textures = new TextureMap().put(TextureKey.ALL, textureId);

        ModelUtils.registerVerticalSlabBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            VERTICAL_SLAB_MODEL
        );
    }
}
