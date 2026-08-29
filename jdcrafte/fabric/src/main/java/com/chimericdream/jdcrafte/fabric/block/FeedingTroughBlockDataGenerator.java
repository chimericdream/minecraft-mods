package com.chimericdream.jdcrafte.fabric.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.jdcrafte.block.FeedingTroughBlock;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FeedingTroughBlockDataGenerator implements FabricBlockDataGenerator {
    private static final Direction.Axis[] HORIZONTAL_AXES = {Direction.Axis.X, Direction.Axis.Z};

    protected final Block block;

    public FeedingTroughBlockDataGenerator(Block block) {
        this.block = block;
    }

    private static Identifier modelId(String name) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/" + name);
    }

    private static Quadrant rotationFor(Direction.Axis axis) {
        // The model (models/block/trough.json) is authored with its long axis running along X.
        return switch (axis) {
            case X -> Quadrant.R0;
            case Z -> Quadrant.R90;
            default -> throw new IllegalArgumentException("Feeding trough only supports horizontal axes, got " + axis);
        };
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE).add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Feeding Trough");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        MultiVariant baseModel = BlockModelGenerators.plainVariant(modelId("trough"));

        Map<String, MultiVariant> contentModels = new HashMap<>();
        for (int level = 1; level <= 3; level++) {
            for (FeedingTroughBlock.FoodType food : FeedingTroughBlock.FoodType.values()) {
                String key = contentKey(level, food);
                contentModels.put(key, BlockModelGenerators.plainVariant(modelId("trough_level" + key)));
            }
        }

        blockStateModelGenerator.registerSimpleItemModel(block, modelId("trough"));

        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        for (Direction.Axis axis : HORIZONTAL_AXES) {
            VariantMutator rotation = VariantMutator.Y_ROT.withValue(rotationFor(axis));

            generator.with(
                new ConditionBuilder().term(FeedingTroughBlock.AXIS, axis),
                baseModel.with(rotation)
            );

            for (int level = 1; level <= 3; level++) {
                for (FeedingTroughBlock.FoodType food : FeedingTroughBlock.FoodType.values()) {
                    generator.with(
                        new ConditionBuilder()
                            .term(FeedingTroughBlock.AXIS, axis)
                            .term(FeedingTroughBlock.LEVEL, level)
                            .term(FeedingTroughBlock.FOOD, food),
                        contentModels.get(contentKey(level, food)).with(rotation)
                    );
                }
            }
        }

        blockStateModelGenerator.blockStateOutput.accept(generator);
    }

    private static String contentKey(int level, FeedingTroughBlock.FoodType food) {
        return level + "_" + food.getSerializedName();
    }
}
