package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;
import java.util.function.Function;

public class ArmoireBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected final ArmoireBlock BLOCK;

    protected static final ModelTemplate BOTTOM_MODEL = makeModel("block/furniture/armoires/bottom");
    protected static final ModelTemplate TOP_MODEL = makeModel("block/furniture/armoires/top");
    protected static final ModelTemplate ITEM_MODEL = makeModel("item/furniture/armoire");

    public ArmoireBlockDataGenerator(Block block) {
        BLOCK = (ArmoireBlock) block;
    }

    protected static ModelTemplate makeModel(String id) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            BlockConfig.RenderType.CUTOUT,
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id)),
            Optional.empty(),
            MinekeaTextures.BAR,
            MinekeaTextures.MATERIAL,
            MinekeaTextures.PLANKS
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block slabIngredient = BLOCK.config.getIngredient("slab");
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("BSB")
            .pattern("BCB")
            .pattern("###")
            .define('B', slabIngredient)
            .define('C', Items.CHEST)
            .define('S', Items.ARMOR_STAND)
            .define('#', plankIngredient)
            .unlockedBy(RecipeProvider.getHasName(slabIngredient),
                generator.has(slabIngredient))
            .unlockedBy(RecipeProvider.getHasName(Items.CHEST),
                generator.has(Items.CHEST))
            .unlockedBy(RecipeProvider.getHasName(Items.ARMOR_STAND),
                generator.has(Items.ARMOR_STAND))
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(BLOCK, generator.createSinglePropConditionTable(BLOCK, ArmoireBlock.HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Armor-oire", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Armor-oire", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.BAR, BuiltInRegistries.BLOCK.getKey(Blocks.NETHERITE_BLOCK).withPrefix("block/"))
            .put(MinekeaTextures.MATERIAL, BuiltInRegistries.BLOCK.getKey(BLOCK.config.getIngredient("log")).withPrefix("block/"))
            .put(MinekeaTextures.PLANKS, BuiltInRegistries.BLOCK.getKey(Blocks.OAK_PLANKS).withPrefix("block/"));

        Identifier bottomModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier itemModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_item", ITEM_MODEL, unused -> textures);

        MultiVariant bottomModel = BlockModelGenerators.plainVariant(bottomModelId);
        MultiVariant topModel = BlockModelGenerators.plainVariant(topModelId);

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, itemModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(ArmoireBlock.FACING, ArmoireBlock.HALF)
                            .select(Direction.NORTH, DoubleBlockHalf.LOWER, bottomModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.NORTH, DoubleBlockHalf.UPPER, topModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.SOUTH, DoubleBlockHalf.LOWER, bottomModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.SOUTH, DoubleBlockHalf.UPPER, topModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.EAST, DoubleBlockHalf.LOWER, bottomModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.EAST, DoubleBlockHalf.UPPER, topModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.WEST, DoubleBlockHalf.LOWER, bottomModel)
                            .select(Direction.WEST, DoubleBlockHalf.UPPER, topModel)
                    )
            );
    }
}
