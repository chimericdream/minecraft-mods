package com.chimericdream.jdcrafte.fabric.data;

import com.chimericdream.jdcrafte.block.ModBlocks;
import com.chimericdream.jdcrafte.fabric.block.FeedingTroughBlockDataGenerator;
import com.chimericdream.jdcrafte.fabric.block.TrellisBlockDataGenerator;
import com.chimericdream.jdcrafte.fabric.block.WeathervaneBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    private static final List<FabricBlockDataGenerator> BLOCK_GENERATORS = buildBlockGenerators();

    private static List<FabricBlockDataGenerator> buildBlockGenerators() {
        List<FabricBlockDataGenerator> generators = new ArrayList<>();

        generators.add(new FeedingTroughBlockDataGenerator(ModBlocks.FEEDING_TROUGH.get()));
        generators.add(new WeathervaneBlockDataGenerator(ModBlocks.WEATHERVANE.get()));

        for (RegistrySupplier<Block> trellis : ModBlocks.TRELLIS_BLOCKS) {
            generators.add(new TrellisBlockDataGenerator(trellis.get()));
        }

        return generators;
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(JDCrafteModelGenerator::new);
        pack.addProvider(JDCrafteBlockLootTables::new);
        pack.addProvider(JDCrafteRecipeProvider::new);
        pack.addProvider(JDCrafteEnglishLangProvider::new);
        pack.addProvider(JDCrafteBlockTagGenerator::new);
    }

    private static class JDCrafteBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        public JDCrafteBlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureBlockTags(arg, this::builder);
            }
        }
    }

    private static class JDCrafteEnglishLangProvider extends FabricLanguageProvider {
        protected JDCrafteEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureTranslations(registryLookup, translationBuilder);
            }
        }
    }

    private static class JDCrafteBlockLootTables extends FabricBlockLootSubProvider {
        private final HolderLookup.Provider registryLookup;

        protected JDCrafteBlockLootTables(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

    private static class JDCrafteRecipeProvider extends FabricRecipeProvider {
        public JDCrafteRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
            return new RecipeProvider(registryLookup, exporter) {
                @Override
                public void buildRecipes() {
                    // MC 26.2 binds item data components lazily during a server reload rather than at
                    // bootstrap, so recipes that read component defaults would otherwise throw
                    // "Components not bound yet". See CLAUDE.md's "Datagen gotcha" section.
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
            return "JDCrafteRecipeProvider";
        }
    }

    private static class JDCrafteModelGenerator extends FabricModelProvider {
        private JDCrafteModelGenerator(FabricPackOutput generator) {
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
            return "JDCrafteModelGenerator";
        }
    }
}
