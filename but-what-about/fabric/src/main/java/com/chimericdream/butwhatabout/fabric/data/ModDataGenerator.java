package com.chimericdream.butwhatabout.fabric.data;

import com.chimericdream.butwhatabout.block.BlockFamilies;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.family.BlockFamilyDataGenerators;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    private static final List<FabricBlockDataGenerator> BLOCK_GENERATORS = new ArrayList<>();

    static {
        BlockFamilies.ALL.forEach(family -> BLOCK_GENERATORS.addAll(BlockFamilyDataGenerators.of(family)));
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ButWhatAboutModelGenerator::new);
        pack.addProvider(ButWhatAboutBlockLootTables::new);
        pack.addProvider(ButWhatAboutRecipeProvider::new);
        pack.addProvider(ButWhatAboutEnglishLangProvider::new);
        pack.addProvider(ButWhatAboutBlockTagGenerator::new);
    }

    private static class ButWhatAboutRecipeProvider extends FabricRecipeProvider {
        public ButWhatAboutRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
            return new RecipeProvider(registryLookup, exporter) {
                @Override
                public void buildRecipes() {
                    // MC 26.2 binds item data components lazily during a ReloadableServerResources reload rather
                    // than at bootstrap, so during datagen Item.components() throws "Components not bound yet"
                    // unless we bind them here first, the same way the server reload does.
                    BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup)
                        .forEach(pending -> pending.apply());

                    for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                        blockGenerator.configureRecipes(registryLookup, exporter, this);
                    }
                }
            };
        }

        @Override
        public @NotNull String getName() {
            return "ButWhatAboutRecipeProvider";
        }
    }

    private static class ButWhatAboutBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        public ButWhatAboutBlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureBlockTags(arg, this::builder);
            }
        }
    }

    private static class ButWhatAboutEnglishLangProvider extends FabricLanguageProvider {
        protected ButWhatAboutEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureTranslations(registryLookup, translationBuilder);
            }
        }
    }

    private static class ButWhatAboutBlockLootTables extends FabricBlockLootSubProvider {
        private final HolderLookup.Provider registryLookup;

        protected ButWhatAboutBlockLootTables(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
            this.registryLookup = registryLookup.join();
        }

        @Override
        public void generate() {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureBlockLootTables(this, this.registryLookup);
            }
        }
    }

    private static class ButWhatAboutModelGenerator extends FabricModelProvider {
        private ButWhatAboutModelGenerator(FabricPackOutput generator) {
            super(generator);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureBlockStateModels(blockStateModelGenerator);
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureItemModels(itemModelGenerator);
            }
        }

        @Override
        public @NotNull String getName() {
            return "ButWhatAboutModelGenerator";
        }
    }
}
