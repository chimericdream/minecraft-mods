package com.chimericdream.minekea.fabric.data;

import com.chimericdream.lib.fabric.data.JarAccess;
import com.chimericdream.lib.fabric.data.TextureGenerator;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.compat.CobbliciousCompat;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Each provider is wrapped so its recipes, loot tables, advancements, and tags work with and without
        // Cobblicious. See CobbliciousDataCompat.
        pack.addProvider((FabricPackOutput output) -> new CobbliciousDataCompat(new MinekeaModelGenerator(output)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new MinekeaBlockLootTables(output, registries)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new MinekeaRecipeProvider(output, registries)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new MinekeaEnglishLangProvider(output, registries)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new MinekeaBlockTagGenerator(output, registries)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new MinekeaItemTagGenerator(output, registries)));
        pack.addProvider((FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) -> new CobbliciousDataCompat(new CobbliciousSwapTagGenerator(output, registries)));

        if (JarAccess.canLoad()) {
            new TextureGenerator(pack, ModInfo.MOD_ID);

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
                    // MC 26.2 binds item data components lazily during a ReloadableServerResources reload rather
                    // than at bootstrap, so during datagen Item.components() (e.g. getDefaultMaxStackSize) throws
                    // "Components not bound yet". Bind them here the same way the server reload does, before any
                    // recipe reads them.
                    BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup)
                        .forEach(pending -> pending.apply());

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

    /**
     * One item tag for each item that Cobblicious replaces. The tag holds the Minekea item and the
     * Cobblicious item, and recipes that need the item use the tag. One of the two items is never
     * registered, so both are optional.
     */
    private static class CobbliciousSwapTagGenerator extends FabricTagsProvider.ItemTagsProvider {
        public CobbliciousSwapTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            CobbliciousCompat.replacements().forEach((minekeaId, cobbliciousId) -> {
                // The warped wart plant block has no item.
                if (minekeaId.getPath().equals("crops/warped_wart/block")) {
                    return;
                }

                this.builder(TagKey.create(Registries.ITEM, CobbliciousDataCompat.swapTag(minekeaId)))
                    .setReplace(false)
                    .addOptional(ResourceKey.create(Registries.ITEM, minekeaId))
                    .addOptional(ResourceKey.create(Registries.ITEM, cobbliciousId));
            });
        }

        @Override
        public @NotNull String getName() {
            return "CobbliciousSwapTagGenerator";
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
