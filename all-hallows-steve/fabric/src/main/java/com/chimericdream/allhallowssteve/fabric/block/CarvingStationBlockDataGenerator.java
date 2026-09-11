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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.DECORATIONS, block)
            .pattern(" P ")
            .pattern("PCP")
            .pattern(" P ")
            .define('P', Items.PUMPKIN)
            .define('C', Blocks.CRAFTING_TABLE)
            .unlockedBy(RecipeProvider.getHasName(Items.PUMPKIN), generator.has(Items.PUMPKIN))
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
