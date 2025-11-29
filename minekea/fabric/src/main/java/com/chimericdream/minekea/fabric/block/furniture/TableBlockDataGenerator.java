package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.tables.TableBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class TableBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private final TableBlock BLOCK;

    protected static final Model CORE_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/table")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model ALL_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_all_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model NORTH_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_north_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model SOUTH_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_south_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model EAST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model WEST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model NORTH_EAST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_north_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model SOUTH_EAST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_south_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model NORTH_WEST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_north_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model SOUTH_WEST_CONNECTED_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/tables/table_south_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );

    public TableBlockDataGenerator(Block block) {
        this.BLOCK = (TableBlock) block;
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
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("XXX")
            .pattern("# #")
            .pattern("# #")
            .input('X', plankIngredient)
            .input('#', logIngredient)
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .criterion(RecipeGenerator.hasItem(logIngredient),
                generator.conditionsFromItem(logIngredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Table", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Table", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.LOG, Registries.BLOCK.getId(logIngredient).withPrefixedPath("block/"))
            .put(MinekeaTextures.PLANKS, Registries.BLOCK.getId(plankIngredient).withPrefixedPath("block/"));

        Identifier coreModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier allConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_all_connected", ALL_CONNECTED_MODEL, unused -> textures);
        Identifier northConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_north", NORTH_CONNECTED_MODEL, unused -> textures);
        Identifier southConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_south", SOUTH_CONNECTED_MODEL, unused -> textures);
        Identifier eastConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_east", EAST_CONNECTED_MODEL, unused -> textures);
        Identifier westConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_west", WEST_CONNECTED_MODEL, unused -> textures);
        Identifier northEastConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_north_east", NORTH_EAST_CONNECTED_MODEL, unused -> textures);
        Identifier southEastConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_south_east", SOUTH_EAST_CONNECTED_MODEL, unused -> textures);
        Identifier northWestConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_north_west", NORTH_WEST_CONNECTED_MODEL, unused -> textures);
        Identifier southWestConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_south_west", SOUTH_WEST_CONNECTED_MODEL, unused -> textures);

        WeightedVariant coreModel = BlockStateModelGenerator.createWeightedVariant(coreModelId);
        WeightedVariant allConnectedModel = BlockStateModelGenerator.createWeightedVariant(allConnectedModelId);
        WeightedVariant northConnectedModel = BlockStateModelGenerator.createWeightedVariant(northConnectedModelId);
        WeightedVariant southConnectedModel = BlockStateModelGenerator.createWeightedVariant(southConnectedModelId);
        WeightedVariant eastConnectedModel = BlockStateModelGenerator.createWeightedVariant(eastConnectedModelId);
        WeightedVariant westConnectedModel = BlockStateModelGenerator.createWeightedVariant(westConnectedModelId);
        WeightedVariant northEastConnectedModel = BlockStateModelGenerator.createWeightedVariant(northEastConnectedModelId);
        WeightedVariant southEastConnectedModel = BlockStateModelGenerator.createWeightedVariant(southEastConnectedModelId);
        WeightedVariant northWestConnectedModel = BlockStateModelGenerator.createWeightedVariant(northWestConnectedModelId);
        WeightedVariant southWestConnectedModel = BlockStateModelGenerator.createWeightedVariant(southWestConnectedModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap.models(TableBlock.NORTH_CONNECTED, TableBlock.SOUTH_CONNECTED, TableBlock.EAST_CONNECTED, TableBlock.WEST_CONNECTED)
                            .register(false, false, false, false, coreModel)
                            .register(true, false, false, false, northConnectedModel)
                            .register(false, false, true, false, eastConnectedModel)
                            .register(false, true, false, false, southConnectedModel)
                            .register(false, false, false, true, westConnectedModel)
                            .register(true, false, true, false, northEastConnectedModel)
                            .register(false, true, true, false, southEastConnectedModel)
                            .register(true, false, false, true, northWestConnectedModel)
                            .register(false, true, false, true, southWestConnectedModel)
                            .register(false, false, true, true, allConnectedModel)
                            .register(true, true, false, false, allConnectedModel)
                            .register(false, true, true, true, allConnectedModel)
                            .register(true, false, true, true, allConnectedModel)
                            .register(true, true, true, false, allConnectedModel)
                            .register(true, true, false, true, allConnectedModel)
                            .register(true, true, true, true, allConnectedModel)
                    )
            );
    }

    public static class TableTooltipDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(TableBlock.TOOLTIP_KEY, "Simple design, but somehow LACKing...");
        }
    }
}
