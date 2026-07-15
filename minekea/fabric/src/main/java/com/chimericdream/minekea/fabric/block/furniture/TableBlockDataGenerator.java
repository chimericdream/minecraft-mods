package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.tables.TableBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class TableBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private final TableBlock BLOCK;

    protected static final ModelTemplate CORE_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/table")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate ALL_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_all_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate NORTH_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_north_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate SOUTH_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_south_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate EAST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate WEST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate NORTH_EAST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_north_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate SOUTH_EAST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_south_east_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate NORTH_WEST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_north_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate SOUTH_WEST_CONNECTED_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/tables/table_south_west_connected")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );

    public TableBlockDataGenerator(Block block) {
        this.BLOCK = (TableBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("XXX")
            .pattern("# #")
            .pattern("# #")
            .define('X', plankIngredient)
            .define('#', logIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Table", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Table", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.LOG, new Material(BuiltInRegistries.BLOCK.getKey(logIngredient).withPrefix("block/")))
            .put(MinekeaTextures.PLANKS, new Material(BuiltInRegistries.BLOCK.getKey(plankIngredient).withPrefix("block/")));

        Identifier coreModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier allConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_all_connected", ALL_CONNECTED_MODEL, unused -> textures);
        Identifier northConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_north", NORTH_CONNECTED_MODEL, unused -> textures);
        Identifier southConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_south", SOUTH_CONNECTED_MODEL, unused -> textures);
        Identifier eastConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_east", EAST_CONNECTED_MODEL, unused -> textures);
        Identifier westConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_west", WEST_CONNECTED_MODEL, unused -> textures);
        Identifier northEastConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_north_east", NORTH_EAST_CONNECTED_MODEL, unused -> textures);
        Identifier southEastConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_south_east", SOUTH_EAST_CONNECTED_MODEL, unused -> textures);
        Identifier northWestConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_north_west", NORTH_WEST_CONNECTED_MODEL, unused -> textures);
        Identifier southWestConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_south_west", SOUTH_WEST_CONNECTED_MODEL, unused -> textures);

        MultiVariant coreModel = BlockModelGenerators.plainVariant(coreModelId);
        MultiVariant allConnectedModel = BlockModelGenerators.plainVariant(allConnectedModelId);
        MultiVariant northConnectedModel = BlockModelGenerators.plainVariant(northConnectedModelId);
        MultiVariant southConnectedModel = BlockModelGenerators.plainVariant(southConnectedModelId);
        MultiVariant eastConnectedModel = BlockModelGenerators.plainVariant(eastConnectedModelId);
        MultiVariant westConnectedModel = BlockModelGenerators.plainVariant(westConnectedModelId);
        MultiVariant northEastConnectedModel = BlockModelGenerators.plainVariant(northEastConnectedModelId);
        MultiVariant southEastConnectedModel = BlockModelGenerators.plainVariant(southEastConnectedModelId);
        MultiVariant northWestConnectedModel = BlockModelGenerators.plainVariant(northWestConnectedModelId);
        MultiVariant southWestConnectedModel = BlockModelGenerators.plainVariant(southWestConnectedModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(TableBlock.NORTH_CONNECTED, TableBlock.SOUTH_CONNECTED, TableBlock.EAST_CONNECTED, TableBlock.WEST_CONNECTED)
                            .select(false, false, false, false, coreModel)
                            .select(true, false, false, false, northConnectedModel)
                            .select(false, false, true, false, eastConnectedModel)
                            .select(false, true, false, false, southConnectedModel)
                            .select(false, false, false, true, westConnectedModel)
                            .select(true, false, true, false, northEastConnectedModel)
                            .select(false, true, true, false, southEastConnectedModel)
                            .select(true, false, false, true, northWestConnectedModel)
                            .select(false, true, false, true, southWestConnectedModel)
                            .select(false, false, true, true, allConnectedModel)
                            .select(true, true, false, false, allConnectedModel)
                            .select(false, true, true, true, allConnectedModel)
                            .select(true, false, true, true, allConnectedModel)
                            .select(true, true, true, false, allConnectedModel)
                            .select(true, true, false, true, allConnectedModel)
                            .select(true, true, true, true, allConnectedModel)
                    )
            );
    }

    public static class TableTooltipDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(TableBlock.TOOLTIP_KEY, "Simple design, but somehow LACKing...");
        }
    }
}
