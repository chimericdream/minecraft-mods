package com.chimericdream.allhallowssteve.fabric.data;

import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreenHandler;
import com.chimericdream.allhallowssteve.fabric.block.CarvingStationBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.DecoratedPumpkinBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    private static final List<FabricBlockDataGenerator> BLOCK_GENERATORS = List.of(
        new CarvingStationBlockDataGenerator(ModBlocks.CARVING_STATION.get()),
        new DecoratedPumpkinBlockDataGenerator(ModBlocks.DECORATED_PUMPKIN.get())
    );

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(AllHallowsSteveModelGenerator::new);
        pack.addProvider(AllHallowsSteveBlockLootTables::new);
        pack.addProvider(AllHallowsSteveRecipeProvider::new);
        pack.addProvider(AllHallowsSteveEnglishLangProvider::new);
    }

    private static class AllHallowsSteveEnglishLangProvider extends FabricLanguageProvider {
        protected AllHallowsSteveEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureTranslations(registryLookup, translationBuilder);
            }

            translationBuilder.add(CarvingStationScreenHandler.SCREEN_ID, "Pumpkin Carving Station");
        }
    }

    private static class AllHallowsSteveRecipeProvider extends FabricRecipeProvider {
        public AllHallowsSteveRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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
            return "AllHallowsSteveRecipeProvider";
        }
    }

    private static class AllHallowsSteveBlockLootTables extends FabricBlockLootSubProvider {
        private final HolderLookup.Provider registryLookup;

        protected AllHallowsSteveBlockLootTables(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

    private static class AllHallowsSteveModelGenerator extends FabricModelProvider {
        private AllHallowsSteveModelGenerator(FabricPackOutput generator) {
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
            return "AllHallowsSteveModelGenerator";
        }
    }
}
