package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.CiderPressBlock;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.chimericdream.lib.fabric.blocks.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

/**
 * Like the carving station, the cider press is a drop-self block with a facing-only blockstate, so it
 * reuses {@code CUBE_ORIENTABLE_TOP_BOTTOM} (top/bottom/front/side) and chimeric-lib's
 * {@code ModelUtils.registerBlockWithHorizontalFacing}. The spout is painted into the front texture.
 */
public class CiderPressBlockDataGenerator implements FabricBlockDataGenerator {
    protected final Block block;

    public CiderPressBlockDataGenerator(Block block) {
        this.block = block;
    }

    private static Material texture(String suffix) {
        return new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/cider_press/cider_press_" + suffix));
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE)
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.DECORATIONS, block)
            .pattern(" I ")
            .pattern("SBS")
            .pattern("PPP")
            .define('I', Items.IRON_INGOT)
            .define('S', Items.STICK)
            .define('B', Items.BARREL)
            .define('P', ItemTags.PLANKS)
            .unlockedBy(RecipeProvider.getHasName(Items.BARREL), generator.has(Items.BARREL))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Cider Press");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.BOTTOM, texture("bottom"))
            .put(TextureSlot.FRONT, texture("front"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, CiderPressBlock.FACING, block, modelId);
        blockStateModelGenerator.registerSimpleItemModel(block, modelId);
    }
}
