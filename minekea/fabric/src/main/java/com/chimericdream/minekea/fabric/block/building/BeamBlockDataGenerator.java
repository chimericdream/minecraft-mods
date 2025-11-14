package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.beams.BeamBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_DOWN;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_EAST;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_NORTH;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_SOUTH;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_UP;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_WEST;

public class BeamBlockDataGenerator implements ChimericLibBlockDataGenerator {
    protected static final Model CONNECTED_NORTH_MODEL = makeModel("north");
    protected static final Model CONNECTED_SOUTH_MODEL = makeModel("south");
    protected static final Model CONNECTED_EAST_MODEL = makeModel("east");
    protected static final Model CONNECTED_WEST_MODEL = makeModel("west");
    protected static final Model CONNECTED_UP_MODEL = makeModel("up");
    protected static final Model CONNECTED_DOWN_MODEL = makeModel("down");
    protected static final Model CORE_MODEL = makeModel(Identifier.of(ModInfo.MOD_ID, "block/building/beams/core"));
    protected static final Model ITEM_MODEL = makeModel(Identifier.of(ModInfo.MOD_ID, "item/building/beam"));

    public BeamBlock BLOCK;

    public BeamBlockDataGenerator(Block block) {
        BLOCK = (BeamBlock) block;
    }

    private static Model makeModel(String direction) {
        return makeModel(Identifier.of(ModInfo.MOD_ID, String.format("block/building/beams/connected_%s", direction)));
    }

    private static Model makeModel(Identifier id) {
        return new Model(
            Optional.of(id),
            Optional.empty(),
            TextureKey.SIDE,
            TextureKey.END
        );
    }

    public Block getBlock() {
        return BLOCK;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.BEAMS)
            .setReplace(false)
            .add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {

    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("# #")
            .pattern("# #")
            .pattern("# #")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Beam", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = getTextures();

        Identifier coreModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier northModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_north", CONNECTED_NORTH_MODEL, unused -> textures);
        Identifier southModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_south", CONNECTED_SOUTH_MODEL, unused -> textures);
        Identifier eastModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_east", CONNECTED_EAST_MODEL, unused -> textures);
        Identifier westModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_west", CONNECTED_WEST_MODEL, unused -> textures);
        Identifier upModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_up", CONNECTED_UP_MODEL, unused -> textures);
        Identifier downModelId = blockStateModelGenerator.createSubModel(BLOCK, "_connected_down", CONNECTED_DOWN_MODEL, unused -> textures);

        WeightedVariant coreVariant = BlockStateModelGenerator.createWeightedVariant(coreModelId);
        WeightedVariant northVariant = BlockStateModelGenerator.createWeightedVariant(northModelId);
        WeightedVariant southVariant = BlockStateModelGenerator.createWeightedVariant(southModelId);
        WeightedVariant eastVariant = BlockStateModelGenerator.createWeightedVariant(eastModelId);
        WeightedVariant westVariant = BlockStateModelGenerator.createWeightedVariant(westModelId);
        WeightedVariant upVariant = BlockStateModelGenerator.createWeightedVariant(upModelId);
        WeightedVariant downVariant = BlockStateModelGenerator.createWeightedVariant(downModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                MultipartBlockModelDefinitionCreator.create(BLOCK)
                    .with(coreVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_NORTH, true), northVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_SOUTH, true), southVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_EAST, true), eastVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_WEST, true), westVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_UP, true), upVariant)
                    .with((new MultipartModelConditionBuilder()).put(CONNECTED_DOWN, true), downVariant)
            );
    }

    private TextureMap getTextures() {
        Identifier sideTexture = BLOCK.config.getTexture();
        Identifier endTexture = Optional.ofNullable(BLOCK.config.getTexture("end")).orElse(sideTexture);

        return new TextureMap()
            .put(TextureKey.SIDE, sideTexture)
            .put(TextureKey.END, endTexture);
    }

    @Override
    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
        ITEM_MODEL.upload(
            BLOCK.BLOCK_ID.withPrefixedPath("item/"),
            getTextures(),
            itemModelGenerator.modelCollector
        );
    }

    @Override
    public void generateTextures() {

    }
}
