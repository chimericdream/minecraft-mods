package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.beams.BeamBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
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

import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_DOWN;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_EAST;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_NORTH;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_SOUTH;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_UP;
import static com.chimericdream.minekea.block.building.beams.BeamBlock.CONNECTED_WEST;

public class BeamBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate CONNECTED_NORTH_MODEL = makeModel("north");
    protected static final ModelTemplate CONNECTED_SOUTH_MODEL = makeModel("south");
    protected static final ModelTemplate CONNECTED_EAST_MODEL = makeModel("east");
    protected static final ModelTemplate CONNECTED_WEST_MODEL = makeModel("west");
    protected static final ModelTemplate CONNECTED_UP_MODEL = makeModel("up");
    protected static final ModelTemplate CONNECTED_DOWN_MODEL = makeModel("down");
    protected static final ModelTemplate CORE_MODEL = makeModel(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/building/beams/core"));
    protected static final ModelTemplate ITEM_MODEL = makeModel(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "item/building/beam"));

    public BeamBlock BLOCK;

    public BeamBlockDataGenerator(Block block) {
        BLOCK = (BeamBlock) block;
    }

    private static ModelTemplate makeModel(String direction) {
        return makeModel(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/building/beams/connected_%s", direction)));
    }

    private static ModelTemplate makeModel(Identifier id) {
        return new ModelTemplate(
            Optional.of(id),
            Optional.empty(),
            TextureSlot.SIDE,
            TextureSlot.END
        );
    }

    public Block getBlock() {
        return BLOCK;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.BEAMS)
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("# #")
            .pattern("# #")
            .pattern("# #")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Beam", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Beam", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = getTextures();

        Identifier coreModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier northModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_north", CONNECTED_NORTH_MODEL, unused -> textures);
        Identifier southModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_south", CONNECTED_SOUTH_MODEL, unused -> textures);
        Identifier eastModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_east", CONNECTED_EAST_MODEL, unused -> textures);
        Identifier westModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_west", CONNECTED_WEST_MODEL, unused -> textures);
        Identifier upModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_up", CONNECTED_UP_MODEL, unused -> textures);
        Identifier downModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_connected_down", CONNECTED_DOWN_MODEL, unused -> textures);

        MultiVariant coreVariant = BlockModelGenerators.plainVariant(coreModelId);
        MultiVariant northVariant = BlockModelGenerators.plainVariant(northModelId);
        MultiVariant southVariant = BlockModelGenerators.plainVariant(southModelId);
        MultiVariant eastVariant = BlockModelGenerators.plainVariant(eastModelId);
        MultiVariant westVariant = BlockModelGenerators.plainVariant(westModelId);
        MultiVariant upVariant = BlockModelGenerators.plainVariant(upModelId);
        MultiVariant downVariant = BlockModelGenerators.plainVariant(downModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiPartGenerator.multiPart(BLOCK)
                    .with(coreVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_NORTH, true), northVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_SOUTH, true), southVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_EAST, true), eastVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_WEST, true), westVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_UP, true), upVariant)
                    .with((new ConditionBuilder()).term(CONNECTED_DOWN, true), downVariant)
            );
    }

    private TextureMapping getTextures() {
        Identifier sideTexture = BLOCK.config.getTexture();
        Identifier endTexture = Optional.ofNullable(BLOCK.config.getTexture("end")).orElse(sideTexture);

        return new TextureMapping()
            .put(TextureSlot.SIDE, new Material(sideTexture))
            .put(TextureSlot.END, new Material(endTexture));
    }

    @Override
    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        ITEM_MODEL.create(
            BLOCK.BLOCK_ID.withPrefix("item/"),
            getTextures(),
            itemModelGenerator.modelOutput
        );
    }
}
