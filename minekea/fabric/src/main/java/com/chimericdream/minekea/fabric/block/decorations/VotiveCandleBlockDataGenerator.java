package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.decorations.candles.VotiveCandleBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Function;

public class VotiveCandleBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate VOTIVE_ITEM_MODEL = makeModel("block/candles/template_votive_item");
    protected static final ModelTemplate ONE_VOTIVE_MODEL = makeModel("block/candles/template_votive_candle");
    protected static final ModelTemplate ONE_VOTIVE_LIT_MODEL = makeModel("block/candles/template_votive_candle");
    protected static final ModelTemplate TWO_VOTIVES_MODEL = makeModel("block/candles/template_two_votive_candles");
    protected static final ModelTemplate TWO_VOTIVES_LIT_MODEL = makeModel("block/candles/template_two_votive_candles");
    protected static final ModelTemplate THREE_VOTIVES_MODEL = makeModel("block/candles/template_three_votive_candles");
    protected static final ModelTemplate THREE_VOTIVES_LIT_MODEL = makeModel("block/candles/template_three_votive_candles");
    protected static final ModelTemplate FOUR_VOTIVES_MODEL = makeModel("block/candles/template_four_votive_candles");
    protected static final ModelTemplate FOUR_VOTIVES_LIT_MODEL = makeModel("block/candles/template_four_votive_candles");

    public VotiveCandleBlock BLOCK;

    public VotiveCandleBlockDataGenerator(Block block) {
        BLOCK = (VotiveCandleBlock) block;
    }

    protected static ModelTemplate makeModel(String template) {
        return new CustomBlockStateModelSupplier.CustomBlockModel(
            BlockConfig.RenderType.TRANSLUCENT,
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, template)),
            Optional.empty(),
            TextureSlot.CANDLE,
            TextureSlot.SIDE
        );
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.CANDLES).add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
        getBuilder.apply(MinekeaItemTags.VOTIVE_CANDLES).add(BLOCK.asItem().builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.shapeless(RecipeCategory.DECORATIONS, BLOCK, 4)
            .requires(ingredient, 4)
            .requires(Items.GLASS)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .unlockedBy(RecipeProvider.getHasName(Items.GLASS),
                generator.has(Items.GLASS))
            .save(exporter);

//        Item dye;
//        if (BLOCK.color.equals("plain")) {
//            dye = Items.ICE;
//        } else {
//            dye = ColorHelpers.getDye(BLOCK.color);
//        }
//
//        generator.shaped(RecipeCategory.DECORATIONS, BLOCK, 8)
//            .pattern("###")
//            .pattern("#D#")
//            .pattern("###")
//            .define('#', makeTagIngredient(MinekeaItemTags.VOTIVE_CANDLES))
//            .define('D', dye)
//            .unlockedBy("has_any_votive",
//                generator.conditionsFromTag(MinekeaItemTags.VOTIVE_CANDLES))
//            .unlockedBy(RecipeProvider.getHasName(dye),
//                generator.has(dye))
//            .save(exporter, BLOCK.BLOCK_ID.withSuffix("_universal_dyeing").toString());
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(BLOCK, generator.createCandleDrops(BLOCK));
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (BLOCK.color.equals("plain")) {
            translationBuilder.add(BLOCK, "Votive Candle");
            translationBuilder.add(BLOCK.asItem(), "Votive Candle");

            return;
        }

        translationBuilder.add(BLOCK, String.format("%s Votive Candle", ColorHelpers.getName(BLOCK.color)));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Votive Candle", ColorHelpers.getName(BLOCK.color)));
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block ingredient = BLOCK.config.getIngredient();

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.CANDLE, TextureMapping.getBlockTexture(ingredient))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GLASS));
        TextureMapping litTextures = new TextureMapping()
            .put(TextureSlot.CANDLE, TextureMapping.getBlockTexture(ingredient, "_lit"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GLASS));

        Identifier candleItemModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", VOTIVE_ITEM_MODEL, unused -> textures);
        Identifier oneCandleModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_one", ONE_VOTIVE_MODEL, unused -> textures);
        Identifier oneCandleLitModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_one_lit", ONE_VOTIVE_LIT_MODEL, unused -> litTextures);
        Identifier twoCandlesModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_two", TWO_VOTIVES_MODEL, unused -> textures);
        Identifier twoCandlesLitModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_two_lit", TWO_VOTIVES_LIT_MODEL, unused -> litTextures);
        Identifier threeCandlesModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_three", THREE_VOTIVES_MODEL, unused -> textures);
        Identifier threeCandlesLitModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_three_lit", THREE_VOTIVES_LIT_MODEL, unused -> litTextures);
        Identifier fourCandlesModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_four", FOUR_VOTIVES_MODEL, unused -> textures);
        Identifier fourCandlesLitModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_four_lit", FOUR_VOTIVES_LIT_MODEL, unused -> litTextures);

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, candleItemModelId);

        MultiVariant oneCandle = BlockModelGenerators.plainVariant(oneCandleModelId);
        MultiVariant oneLitCandle = BlockModelGenerators.plainVariant(oneCandleLitModelId);
        MultiVariant twoCandles = BlockModelGenerators.plainVariant(twoCandlesModelId);
        MultiVariant twoLitCandles = BlockModelGenerators.plainVariant(twoCandlesLitModelId);
        MultiVariant threeCandles = BlockModelGenerators.plainVariant(threeCandlesModelId);
        MultiVariant threeLitCandles = BlockModelGenerators.plainVariant(threeCandlesLitModelId);
        MultiVariant fourCandles = BlockModelGenerators.plainVariant(fourCandlesModelId);
        MultiVariant fourLitCandles = BlockModelGenerators.plainVariant(fourCandlesLitModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(VotiveCandleBlock.CANDLES, VotiveCandleBlock.LIT)
                            .select(1, false, oneCandle)
                            .select(1, true, oneLitCandle)
                            .select(2, false, twoCandles)
                            .select(2, true, twoLitCandles)
                            .select(3, false, threeCandles)
                            .select(3, true, threeLitCandles)
                            .select(4, false, fourCandles)
                            .select(4, true, fourLitCandles)
                    )
            );
    }
}
