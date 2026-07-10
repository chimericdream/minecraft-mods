package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.minekea.block.containers.crates.TrappedCrateBlock;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class TrappedCrateBlockDataGenerator extends CrateBlockDataGenerator {
    public TrappedCrateBlockDataGenerator(Block block) {
        super(block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shapeless(RecipeCategory.REDSTONE, BLOCK, 1)
            .requires(((TrappedCrateBlock) BLOCK).BASE_CRATE.get())
            .requires(Items.TRIPWIRE_HOOK)
            .unlockedBy(RecipeProvider.getHasName(((TrappedCrateBlock) BLOCK).BASE_CRATE.get()),
                generator.has(((TrappedCrateBlock) BLOCK).BASE_CRATE.get()))
            .unlockedBy(RecipeProvider.getHasName(Items.TRIPWIRE_HOOK),
                generator.has(Items.TRIPWIRE_HOOK))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Trapped %s Crate", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Trapped %s Crate", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.BRACE, BLOCK.config.getTexture("brace"))
            .put(MinekeaTextures.MATERIAL, TextureUtils.block(BLOCK.BLOCK_ID, "_material"));

        configureBlockStateModels(blockStateModelGenerator, textures);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().generate(Identifier.withDefaultNamespace("block"), instance -> {
            final Optional<BufferedImage> source = instance.getImage(String.format("%s_planks", BLOCK.config.getMaterial()));

            if (source.isPresent()) {
                BufferedImage sourceImage = source.get();
                BufferedImage overlayImage = instance.getMinekeaImage("block/crates/trapped_overlay").orElse(null);

                int w = sourceImage.getWidth();
                int h = sourceImage.getHeight();

                BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                Graphics g = combined.getGraphics();
                g.drawImage(sourceImage, 0, 0, null);
                g.drawImage(overlayImage, 0, 0, w, h, null);

                g.dispose();

                instance.generate(BLOCK.BLOCK_ID.withSuffix("_material"), combined);
            }
        });
    }
}
