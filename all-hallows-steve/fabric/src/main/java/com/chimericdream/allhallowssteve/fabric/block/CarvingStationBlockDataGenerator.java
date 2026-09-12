package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.CarvingStationBlock;
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
 * The carving station is a plain drop-self block with a facing-only blockstate (same rotation
 * dispatch as vanilla's furnace/blast furnace), so this reuses {@code ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM}
 * (top/bottom/front/side textures) and chimeric-lib's {@code ModelUtils.registerBlockWithHorizontalFacing}
 * rather than hand-rolling the rotation dispatch.
 */
public class CarvingStationBlockDataGenerator implements FabricBlockDataGenerator {
    protected final Block block;

    public CarvingStationBlockDataGenerator(Block block) {
        this.block = block;
    }

    private static Material texture(String suffix) {
        return new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/carving_station/carving_station_" + suffix));
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
            .pattern("IS")
            .pattern("PC")
            .pattern("PP")
            .define('I', Items.IRON_INGOT)
            .define('S', Items.SHEARS)
            .define('P', ItemTags.PLANKS)
            .define('C', Blocks.CRAFTING_TABLE)
            .unlockedBy(RecipeProvider.getHasName(Items.SHEARS), generator.has(Items.SHEARS))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Pumpkin Carving Station");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.BOTTOM, texture("bottom"))
            .put(TextureSlot.FRONT, texture("front"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, CarvingStationBlock.FACING, block, modelId);
        blockStateModelGenerator.registerSimpleItemModel(block, modelId);
    }
}
