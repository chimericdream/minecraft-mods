package com.chimericdream.minekea.fabric.data;

import com.chimericdream.minekea.fabric.block.ModBlockDataGenerators;
import com.chimericdream.minekea.fabric.item.ModItemDataGenerators;
import com.chimericdream.minekea.fabric.registry.ModRegistryDataGenerator;
import com.chimericdream.minekea.fabric.util.BlockDataGeneratorGroup;
import com.chimericdream.minekea.fabric.util.ItemDataGeneratorGroup;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(MinekeaModelGenerator::new);
        pack.addProvider(MinekeaBlockLootTables::new);
        pack.addProvider(MinekeaRecipeProvider::new);
        pack.addProvider(MinekeaEnglishLangProvider::new);
        pack.addProvider(MinekeaBlockTagGenerator::new);
        pack.addProvider(MinekeaItemTagGenerator::new);

        if (JarAccess.canLoad()) {
            new TextureGenerator(pack);

            ModBlockDataGenerators.BLOCK_GROUPS.forEach(BlockDataGeneratorGroup::generateTextures);
            ModItemDataGenerators.ITEM_GROUPS.forEach(ItemDataGeneratorGroup::generateTextures);
        }
    }

    private static class MinekeaRecipeProvider extends FabricRecipeProvider {
        public MinekeaRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
            return new RecipeProvider(registryLookup, exporter) {
                @Override
                public void buildRecipes() {
                    for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                        group.configureRecipes(registryLookup, exporter, this);
                    }

                    for (ItemDataGeneratorGroup group : ModItemDataGenerators.ITEM_GROUPS) {
                        group.configureRecipes(registryLookup, exporter, this);
                    }

//                    MinekeaMod.ITEMS.configureRecipes(exporter);
                }
            };
        }

        @Override
        public @NotNull String getName() {
            return "MinekeaRecipeProvider";
        }
    }

    private static class MinekeaBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        public MinekeaBlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureBlockTags(arg, this::builder);
            }

//            MinekeaMod.ITEMS.configureBlockTags(arg, this::getOrCreateTagBuilder);
        }
    }

    private static class MinekeaItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {
        public MinekeaItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureItemTags(arg, this::builder);
            }

            for (ItemDataGeneratorGroup group : ModItemDataGenerators.ITEM_GROUPS) {
                group.configureItemTags(arg, this::builder);
            }

//            MinekeaMod.ITEMS.configureItemTags(arg, this::getOrCreateTagBuilder);
        }
    }

    private static class MinekeaEnglishLangProvider extends FabricLanguageProvider {
        protected MinekeaEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureTranslations(registryLookup, translationBuilder);
            }

            for (ItemDataGeneratorGroup group : ModItemDataGenerators.ITEM_GROUPS) {
                group.configureTranslations(registryLookup, translationBuilder);
            }

            ModRegistryDataGenerator.configureTranslations(registryLookup, translationBuilder);

//            MinekeaMod.ITEMS.configureTranslations(registryLookup, translationBuilder);
        }
    }

    private static class MinekeaBlockLootTables extends FabricBlockLootSubProvider {
        private final HolderLookup.Provider registryLookup;

        protected MinekeaBlockLootTables(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
            this.registryLookup = registryLookup.join();
        }

        @Override
        public void generate() {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureBlockLootTables(this, this.registryLookup);
            }

//            MinekeaMod.ITEMS.configureBlockLootTables(this.registryLookup, this);
        }
    }

    private static class MinekeaModelGenerator extends FabricModelProvider {
        private MinekeaModelGenerator(FabricPackOutput generator) {
            super(generator);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureBlockStateModels(blockStateModelGenerator);
            }

//            MinekeaMod.ITEMS.configureBlockStateModels(blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            for (BlockDataGeneratorGroup group : ModBlockDataGenerators.BLOCK_GROUPS) {
                group.configureItemModels(itemModelGenerator);
            }

            for (ItemDataGeneratorGroup group : ModItemDataGenerators.ITEM_GROUPS) {
                group.configureItemModels(itemModelGenerator);
            }

//            MinekeaMod.ITEMS.configureItemModels(itemModelGenerator);
        }

        @Override
        public @NotNull String getName() {
            return "MinekeaModelGenerator";
        }
    }
}
