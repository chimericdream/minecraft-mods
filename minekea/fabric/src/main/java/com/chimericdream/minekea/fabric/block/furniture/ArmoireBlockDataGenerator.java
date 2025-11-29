package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class ArmoireBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected final ArmoireBlock BLOCK;

    protected static final Model BOTTOM_MODEL = makeModel("block/furniture/armoires/bottom");
    protected static final Model TOP_MODEL = makeModel("block/furniture/armoires/top");
    protected static final Model ITEM_MODEL = makeModel("item/furniture/armoire");

    public ArmoireBlockDataGenerator(Block block) {
        BLOCK = (ArmoireBlock) block;
    }

    protected static Model makeModel(String id) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            BlockConfig.RenderType.CUTOUT,
            Optional.of(Identifier.of(ModInfo.MOD_ID, id)),
            Optional.empty(),
            MinekeaTextures.BAR,
            MinekeaTextures.MATERIAL,
            MinekeaTextures.PLANKS
        );
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block slabIngredient = BLOCK.config.getIngredient("slab");
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("BSB")
            .pattern("BCB")
            .pattern("###")
            .input('B', slabIngredient)
            .input('C', Items.CHEST)
            .input('S', Items.ARMOR_STAND)
            .input('#', plankIngredient)
            .criterion(RecipeGenerator.hasItem(slabIngredient),
                generator.conditionsFromItem(slabIngredient))
            .criterion(RecipeGenerator.hasItem(Items.CHEST),
                generator.conditionsFromItem(Items.CHEST))
            .criterion(RecipeGenerator.hasItem(Items.ARMOR_STAND),
                generator.conditionsFromItem(Items.ARMOR_STAND))
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK, generator.dropsWithProperty(BLOCK, ArmoireBlock.HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Armor-oire", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Armor-oire", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.BAR, Registries.BLOCK.getId(Blocks.NETHERITE_BLOCK).withPrefixedPath("block/"))
            .put(MinekeaTextures.MATERIAL, Registries.BLOCK.getId(BLOCK.config.getIngredient("log")).withPrefixedPath("block/"))
            .put(MinekeaTextures.PLANKS, Registries.BLOCK.getId(Blocks.OAK_PLANKS).withPrefixedPath("block/"));

        Identifier bottomModelId = blockStateModelGenerator.createSubModel(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSubModel(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier itemModelId = blockStateModelGenerator.createSubModel(BLOCK, "_item", ITEM_MODEL, unused -> textures);

        WeightedVariant bottomModel = BlockStateModelGenerator.createWeightedVariant(bottomModelId);
        WeightedVariant topModel = BlockStateModelGenerator.createWeightedVariant(topModelId);

        blockStateModelGenerator.registerParentedItemModel(BLOCK, itemModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap.models(ArmoireBlock.FACING, ArmoireBlock.HALF)
                            .register(Direction.NORTH, DoubleBlockHalf.LOWER, bottomModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                            .register(Direction.NORTH, DoubleBlockHalf.UPPER, topModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                            .register(Direction.SOUTH, DoubleBlockHalf.LOWER, bottomModel)
                            .register(Direction.SOUTH, DoubleBlockHalf.UPPER, topModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                            .register(Direction.EAST, DoubleBlockHalf.LOWER, bottomModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(Direction.EAST, DoubleBlockHalf.UPPER, topModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(Direction.WEST, DoubleBlockHalf.LOWER, bottomModel)
                            .register(Direction.WEST, DoubleBlockHalf.UPPER, topModel)
                    )
            );
    }
}
