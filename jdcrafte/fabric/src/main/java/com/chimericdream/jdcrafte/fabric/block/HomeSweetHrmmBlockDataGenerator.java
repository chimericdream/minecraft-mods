package com.chimericdream.jdcrafte.fabric.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.jdcrafte.block.HomeSweetHrmmBlock;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

/**
 * One instance covers a single wood type's Home Sweet Hrmmm (see {@code ModBlocks.HOME_SWEET_HRMM_BLOCKS}),
 * matching {@code TrellisBlockDataGenerator}'s per-material model-template approach - except this block
 * needs three templates, one per hand-authored model file: {@link #LEFT_MODEL}/{@link #RIGHT_MODEL} for
 * the two block halves and {@link #ITEM_MODEL} for the item's own (non-split) 3D icon. All three share
 * the {@link #SIGN} texture slot, resolved here to the material's plank texture; the "overlay" ("Home
 * Sweet Hrmmm" decal) texture is baked into the template files themselves since it's the same for every
 * material.
 *
 * <p>Only {@link HomeSweetHrmmBlock.Side#LEFT}'s state actually drops an item - see {@link
 * HomeSweetHrmmBlock}'s class doc, same trick as {@code TrellisArchBlockDataGenerator}/vanilla's bed
 * loot tables.
 */
public class HomeSweetHrmmBlockDataGenerator implements FabricBlockDataGenerator {
    private static final TextureSlot SIGN = TextureSlot.create("sign");

    private static final ModelTemplate LEFT_MODEL = new ModelTemplate(
        Optional.of(modelId("home_sweet_hrmm_left")),
        Optional.empty(),
        SIGN
    );

    private static final ModelTemplate RIGHT_MODEL = new ModelTemplate(
        Optional.of(modelId("home_sweet_hrmm_right")),
        Optional.empty(),
        SIGN
    );

    private static final ModelTemplate ITEM_MODEL = new ModelTemplate(
        Optional.of(itemModelId("home_sweet_hrmm")),
        Optional.empty(),
        SIGN
    );

    protected final HomeSweetHrmmBlock block;

    public HomeSweetHrmmBlockDataGenerator(Block block) {
        this.block = (HomeSweetHrmmBlock) block;
    }

    private static Identifier modelId(String name) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/" + name);
    }

    private static Identifier itemModelId(String name) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "item/" + name);
    }

    // Same identity orientation as TrellisBlock/TrellisArchBlock: authored for FACING = NORTH.
    private static Quadrant rotationFor(Direction facing) {
        return switch (facing) {
            case NORTH -> Quadrant.R0;
            case EAST -> Quadrant.R90;
            case SOUTH -> Quadrant.R180;
            case WEST -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Home Sweet Hrmm only supports horizontal facings, got " + facing);
        };
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE).add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(block, generator.createSinglePropConditionTable(block, HomeSweetHrmmBlock.SIDE, HomeSweetHrmmBlock.Side.LEFT));
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Identifier signId = Identifier.withDefaultNamespace(block.config.getMaterial() + "_sign");
        Item sign = BuiltInRegistries.ITEM.getValue(signId);

        generator.shaped(RecipeCategory.DECORATIONS, block)
            .pattern("E")
            .pattern("S")
            .define('E', Items.EMERALD)
            .define('S', sign)
            .unlockedBy(RecipeProvider.getHasName(sign), generator.has(sign))
            .unlockedBy(RecipeProvider.getHasName(Items.EMERALD), generator.has(Items.EMERALD))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, block.config.getMaterialName() + " Home Sweet Hrmmm");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping().put(SIGN, new Material(block.config.getTexture()));

        Identifier leftModelId = blockStateModelGenerator.createSuffixedVariant(block, "_left", LEFT_MODEL, unused -> textures);
        Identifier rightModelId = blockStateModelGenerator.createSuffixedVariant(block, "_right", RIGHT_MODEL, unused -> textures);
        Identifier itemModelId = ITEM_MODEL.create(block.asItem(), textures, blockStateModelGenerator.modelOutput);

        MultiVariant leftModel = BlockModelGenerators.plainVariant(leftModelId);
        MultiVariant rightModel = BlockModelGenerators.plainVariant(rightModelId);

        blockStateModelGenerator.registerSimpleItemModel(block, itemModelId);

        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            VariantMutator rotation = VariantMutator.Y_ROT.withValue(rotationFor(facing));

            generator.with(
                new ConditionBuilder().term(HomeSweetHrmmBlock.FACING, facing).term(HomeSweetHrmmBlock.SIDE, HomeSweetHrmmBlock.Side.LEFT),
                leftModel.with(rotation)
            );
            generator.with(
                new ConditionBuilder().term(HomeSweetHrmmBlock.FACING, facing).term(HomeSweetHrmmBlock.SIDE, HomeSweetHrmmBlock.Side.RIGHT),
                rightModel.with(rotation)
            );
        }

        blockStateModelGenerator.blockStateOutput.accept(generator);
    }
}
