package com.chimericdream.jdcrafte.fabric.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.jdcrafte.block.TrellisArchBlock;
import com.chimericdream.jdcrafte.block.TrellisBlock;
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
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

/**
 * One instance covers a single wood type's trellis arch (see {@code ModBlocks.TRELLIS_ARCH_BLOCKS}),
 * matching {@code TrellisBlockDataGenerator}'s per-material model-template approach - except this
 * block needs two templates, one per model file ({@link #ARCH_MODEL} for {@code trellis_arch.json},
 * used for {@link TrellisArchBlock.Part#FRONT}/{@link TrellisArchBlock.Part#BACK}, and {@link
 * #ARCH_TOP_MODEL} for {@code trellis_arch_top.json}, used for {@link TrellisArchBlock.Part#CENTER}).
 */
public class TrellisArchBlockDataGenerator implements FabricBlockDataGenerator {
    private static final TextureSlot LOG = TextureSlot.create("log");
    private static final TextureSlot LOG_TOP = TextureSlot.create("log_top");

    private static final ModelTemplate ARCH_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/trellis_arch")),
        Optional.empty(),
        LOG, LOG_TOP
    );

    private static final ModelTemplate ARCH_TOP_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/trellis_arch_top")),
        Optional.empty(),
        LOG, LOG_TOP
    );

    protected final TrellisArchBlock block;

    public TrellisArchBlockDataGenerator(Block block) {
        this.block = (TrellisArchBlock) block;
    }

    // Same identity orientation as TrellisBlock: authored for FACING = NORTH.
    private static Quadrant rotationFor(Direction facing) {
        return switch (facing) {
            case NORTH -> Quadrant.R0;
            case EAST -> Quadrant.R90;
            case SOUTH -> Quadrant.R180;
            case WEST -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Trellis arch only supports horizontal facings, got " + facing);
        };
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.CLIMBABLE).add(block.builtInRegistryHolder().key());
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE).add(block.builtInRegistryHolder().key());
    }

    // Only the CENTER part's state drops an item - same trick as vanilla's bed loot tables (conditioned
    // on BedPart.HEAD). Any break of the 3-block structure always destroys CENTER too, either directly
    // or via TrellisArchBlock#updateShape's cascade, so exactly one item drops regardless of which of
    // the 3 blocks the player targets. See TrellisArchBlock#playerWillDestroy for the creative-mode
    // half of this (the cascade's own drop isn't gamemode-aware, so it needs pre-empting there).
    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(block, generator.createSinglePropConditionTable(block, TrellisArchBlock.PART, TrellisArchBlock.Part.CENTER));
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block trellis = BuiltInRegistries.BLOCK.getValue(TrellisBlock.makeId(block.config.getMaterial()));

        generator.shaped(RecipeCategory.DECORATIONS, block, 3)
            .pattern(" T ")
            .pattern("T T")
            .define('T', trellis)
            .unlockedBy(RecipeProvider.getHasName(trellis), generator.has(trellis))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, block.config.getMaterialName() + " Trellis Arch");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier logTexture = BuiltInRegistries.BLOCK.getKey(block.config.getIngredient("log")).withPrefix("block/");
        Identifier logTopTexture = Identifier.fromNamespaceAndPath(logTexture.getNamespace(), logTexture.getPath() + "_top");

        TextureMapping textures = new TextureMapping()
            .put(LOG, new Material(logTexture))
            .put(LOG_TOP, new Material(logTopTexture));

        Identifier sideModelId = blockStateModelGenerator.createSuffixedVariant(block, "", ARCH_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSuffixedVariant(block, "_top", ARCH_TOP_MODEL, unused -> textures);

        MultiVariant sideModel = BlockModelGenerators.plainVariant(sideModelId);
        MultiVariant topModel = BlockModelGenerators.plainVariant(topModelId);

        blockStateModelGenerator.registerSimpleItemModel(block, topModelId);

        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            VariantMutator rotation = VariantMutator.Y_ROT.withValue(rotationFor(facing));
            VariantMutator mirroredRotation = VariantMutator.Y_ROT.withValue(rotationFor(facing.getOpposite()));

            generator.with(
                new ConditionBuilder().term(TrellisArchBlock.FACING, facing).term(TrellisArchBlock.PART, TrellisArchBlock.Part.CENTER),
                topModel.with(rotation)
            );
            generator.with(
                new ConditionBuilder().term(TrellisArchBlock.FACING, facing).term(TrellisArchBlock.PART, TrellisArchBlock.Part.FRONT),
                sideModel.with(rotation)
            );
            generator.with(
                new ConditionBuilder().term(TrellisArchBlock.FACING, facing).term(TrellisArchBlock.PART, TrellisArchBlock.Part.BACK),
                sideModel.with(mirroredRotation)
            );
        }

        blockStateModelGenerator.blockStateOutput.accept(generator);
    }
}
