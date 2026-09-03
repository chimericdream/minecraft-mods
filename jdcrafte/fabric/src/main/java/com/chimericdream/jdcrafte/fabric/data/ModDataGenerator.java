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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
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
