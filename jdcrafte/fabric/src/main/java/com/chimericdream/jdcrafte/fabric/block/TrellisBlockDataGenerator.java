package com.chimericdream.jdcrafte.fabric.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.jdcrafte.block.TrellisBlock;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

/**
 * One instance covers a single wood type's trellis (see {@code ModBlocks.TRELLIS_BLOCKS}). The
 * geometry lives once in {@code models/block/trellis.json}, authored as a template - its elements
 * reference the {@code #log}/{@code #log_top} texture variables resolved here, rather than a literal
 * texture - and {@link #TRELLIS_MODEL} stamps out one concrete model per material via {@link
 * BlockModelGenerators#createSuffixedVariant}, same as minekea's {@code ChairBlockDataGenerator}.
 */
public class TrellisBlockDataGenerator implements FabricBlockDataGenerator {
    private static final TextureSlot LOG = TextureSlot.create("log");
    private static final TextureSlot LOG_TOP = TextureSlot.create("log_top");

    private static final ModelTemplate TRELLIS_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/trellis")),
        Optional.empty(),
        LOG, LOG_TOP
    );

    protected final TrellisBlock block;

    public TrellisBlockDataGenerator(Block block) {
        this.block = (TrellisBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.CLIMBABLE).add(block.builtInRegistryHolder().key());
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE).add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, block.config.getMaterialName() + " Trellis");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier logTexture = BuiltInRegistries.BLOCK.getKey(block.config.getIngredient("log")).withPrefix("block/");
        Identifier logTopTexture = Identifier.fromNamespaceAndPath(logTexture.getNamespace(), logTexture.getPath() + "_top");

        TextureMapping textures = new TextureMapping()
            .put(LOG, new Material(logTexture))
            .put(LOG_TOP, new Material(logTopTexture));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", TRELLIS_MODEL, unused -> textures);
        MultiVariant baseModel = BlockModelGenerators.plainVariant(modelId);

        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(
                    PropertyDispatch.initial(TrellisBlock.FACING)
                        .select(Direction.NORTH, baseModel)
                        .select(Direction.EAST, baseModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, baseModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                        .select(Direction.WEST, baseModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                )
        );
    }
}
