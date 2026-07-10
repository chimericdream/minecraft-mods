package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.storage.ItemStorageBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class ItemStorageBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public final ItemStorageBlock BLOCK;

    public ItemStorageBlockDataGenerator(Block block) {
        this.BLOCK = (ItemStorageBlock) block;
    }

    protected static ModelTemplate makeCubeModel(BlockConfig.RenderType renderType) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            renderType,
            Optional.of(Identifier.withDefaultNamespace("block/cube_all")),
            Optional.empty(),
            TextureSlot.ALL
        );
    }

    protected static ModelTemplate makeColumnModel(BlockConfig.RenderType renderType) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            renderType,
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/compressed_column")),
            Optional.empty(),
            TextureSlot.BOTTOM,
            TextureSlot.SIDE,
            TextureSlot.TOP
        );
    }

    protected static ModelTemplate makeBaggedModel(BlockConfig.RenderType renderType) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            renderType,
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/bagged_block")),
            Optional.empty(),
            MinekeaTextures.CONTENTS
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item, Item>> getBuilder) {
        if (BLOCK.isBaggedItem) {
            getBuilder.apply(MinekeaItemTags.BAGGED_ITEMS)
                .setReplace(false)
                .add(BLOCK.asItem());
        }
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item baseItem = BLOCK.config.getItem();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', baseItem)
            .unlockedBy(RecipeProvider.getHasName(baseItem),
                generator.has(baseItem))
            .save(exporter);

        // This means that things like totems won't be uncraftable; modpacks which have some method to override
        // the max stack size can re-add these recipes in a datapack
        if (baseItem.getDefaultMaxStackSize() >= 9) {
            generator.shapeless(RecipeCategory.BUILDING_BLOCKS, baseItem, 9)
                .requires(BLOCK)
                .unlockedBy(RecipeProvider.getHasName(BLOCK),
                    generator.has(BLOCK))
                .save(exporter, BuiltInRegistries.ITEM.getKey(baseItem).withSuffix("_from_compressed").toString());
        }
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (BLOCK.config.getName() != null) {
            translationBuilder.add(BLOCK, BLOCK.config.getName());
            translationBuilder.add(BLOCK.asItem(), BLOCK.config.getName());
        } else {
            translationBuilder.add(BLOCK, String.format("Compressed %s", BLOCK.config.getMaterialName()));
            translationBuilder.add(BLOCK.asItem(), String.format("Compressed %s", BLOCK.config.getMaterialName()));
        }
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBaggedBlockModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.CONTENTS, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s", BLOCK.BLOCK_ID.getPath())))
            .put(TextureSlot.ALL, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s", BLOCK.BLOCK_ID.getPath())));

        configureBaggedBlockModels(blockStateModelGenerator, textures);
    }

    public void configureBaggedBlockModels(BlockModelGenerators blockStateModelGenerator, TextureMapping textures) {
        Identifier baggedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_bagged", makeBaggedModel(BlockConfig.RenderType.CUTOUT), unused -> textures);
        Identifier baseModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", ModelTemplates.CUBE_ALL, unused -> textures);

        MultiVariant baggedModel = BlockModelGenerators.plainVariant(baggedModelId);
        MultiVariant baseModel = BlockModelGenerators.plainVariant(baseModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(PropertyDispatch.initial(ItemStorageBlock.IS_BAGGED)
                        .select(true, baggedModel)
                        .select(false, baseModel))
            );

//        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(BLOCK);
    }

    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        if (BLOCK.isBaggedItem) {
            Block self = BLOCK;

            itemModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(BLOCK.asItem()), new ModelInstance() {
                @Override
                public JsonElement get() {
                    JsonArray overrides = new JsonArray();

                    JsonObject override = new JsonObject();
                    JsonObject predicate = new JsonObject();
                    predicate.addProperty("custom_model_data", 9001);
                    override.add("predicate", predicate);
                    override.addProperty("model", ModelLocationUtils.getModelLocation(self).withSuffix("_bagged").toString());

                    overrides.add(override);

                    JsonObject modelJson = new JsonObject();
                    modelJson.addProperty("parent", ModelLocationUtils.getModelLocation(self).toString());
                    modelJson.add("overrides", overrides);

                    return modelJson;
                }
            });
        }
    }

    protected void configureCustomBaggedBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier contentsTexture = BLOCK.config.getTexture("contents");
        Identifier allTexture = BLOCK.config.getTexture("all");

        if (contentsTexture == null || allTexture == null) {
            throw new RuntimeException("Missing textures for block model");
        }

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.CONTENTS, contentsTexture)
            .put(TextureSlot.ALL, allTexture);

        configureBaggedBlockModels(blockStateModelGenerator, textures);
    }

    protected void configureBlockStateModelsWithFacing(BlockModelGenerators blockStateModelGenerator) {
        Identifier bottomTexture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_bottom", BLOCK.BLOCK_ID.getPath()));
        Identifier sideTexture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_side", BLOCK.BLOCK_ID.getPath()));
        Identifier topTexture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_top", BLOCK.BLOCK_ID.getPath()));

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, bottomTexture)
            .put(TextureSlot.SIDE, sideTexture)
            .put(TextureSlot.TOP, topTexture);

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", makeColumnModel(BLOCK.config.getRenderType()), unused -> textures);

        ModelUtils.registerBlockWithFacing(blockStateModelGenerator, ItemStorageBlock.FACING, BLOCK, subModelId);
    }

    protected void configureBlockStateModelsWithAxis(BlockModelGenerators blockStateModelGenerator) {
        Identifier endTexture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_end", BLOCK.BLOCK_ID.getPath()));
        Identifier sideTexture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/%s_side", BLOCK.BLOCK_ID.getPath()));

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, endTexture)
            .put(TextureSlot.SIDE, sideTexture)
            .put(TextureSlot.TOP, endTexture);

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", makeColumnModel(BLOCK.config.getRenderType()), unused -> textures);

        ModelUtils.registerBlockWithAxis(blockStateModelGenerator, ItemStorageBlock.AXIS, BLOCK, subModelId);
    }

    protected void configureDefaultBlockStateModel(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping().put(TextureSlot.ALL, TextureUtils.block(BLOCK));
        blockStateModelGenerator.createTrivialBlock(
            BLOCK,
            TexturedModel.createDefault((unused) -> textures, makeCubeModel(BLOCK.config.getRenderType()))
        );
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        switch (BLOCK.model) {
            case CUSTOM:
                configureCustomBaggedBlockStateModels(blockStateModelGenerator);
                break;
            case FACING:
                configureBlockStateModelsWithFacing(blockStateModelGenerator);
                break;
            case AXIS:
                configureBlockStateModelsWithAxis(blockStateModelGenerator);
                break;
            case BAGGED:
                configureBaggedBlockModels(blockStateModelGenerator);
                break;
            case DEFAULT:
            default:
                configureDefaultBlockStateModel(blockStateModelGenerator);
        }
    }
}
