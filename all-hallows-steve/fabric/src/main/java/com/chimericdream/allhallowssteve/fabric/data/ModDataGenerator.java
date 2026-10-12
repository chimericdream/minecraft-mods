package com.chimericdream.allhallowssteve.fabric.data;

import com.chimericdream.allhallowssteve.block.CandleLitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.LitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.PumpkinContents;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreenHandler;
import com.chimericdream.allhallowssteve.fabric.advancement.AllHallowsSteveAdvancements;
import com.chimericdream.allhallowssteve.fabric.block.CandleLitDecoratedPumpkinBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.CarvingStationBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.JuiceCauldronBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.CiderPressBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.DecoratedPumpkinBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.block.LitDecoratedPumpkinBlockDataGenerator;
import com.chimericdream.allhallowssteve.fabric.item.CiderItemDataGenerator;
import com.chimericdream.allhallowssteve.fabric.item.PumpkinStencilItemDataGenerator;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.client.screen.CiderPressScreenHandler;
import com.chimericdream.allhallowssteve.tag.ModTags;
import com.chimericdream.allhallowssteve.fabric.item.SpiceItemDataGenerator;
import com.chimericdream.allhallowssteve.villager.ModVillagerTrades;
import com.chimericdream.allhallowssteve.tags.AHSTags;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    private static final List<FabricBlockDataGenerator> BLOCK_GENERATORS = List.of(
        new CarvingStationBlockDataGenerator(ModBlocks.CARVING_STATION.get()),
        new CiderPressBlockDataGenerator(ModBlocks.CIDER_PRESS.get()),
        new JuiceCauldronBlockDataGenerator(ModBlocks.APPLE_CIDER_CAULDRON.get(), "Apple Cider Cauldron"),
        new JuiceCauldronBlockDataGenerator(ModBlocks.MULLED_APPLE_CIDER_CAULDRON.get(), "Mulled Apple Cider Cauldron"),
        new JuiceCauldronBlockDataGenerator(ModBlocks.SWEET_BERRY_JUICE_CAULDRON.get(), "Sweet Berry Juice Cauldron"),
        new JuiceCauldronBlockDataGenerator(ModBlocks.MULLED_SWEET_BERRY_JUICE_CAULDRON.get(), "Mulled Sweet Berry Juice Cauldron"),
        new JuiceCauldronBlockDataGenerator(ModBlocks.GLOW_BERRY_JUICE_CAULDRON.get(), "Glow Berry Juice Cauldron"),
        new JuiceCauldronBlockDataGenerator(ModBlocks.MULLED_GLOW_BERRY_JUICE_CAULDRON.get(), "Mulled Glow Berry Juice Cauldron"),
        new DecoratedPumpkinBlockDataGenerator(ModBlocks.DECORATED_PUMPKIN.get()),
        new LitDecoratedPumpkinBlockDataGenerator((LitDecoratedPumpkinBlock) ModBlocks.LIT_DECORATED_PUMPKIN.get()),
        new LitDecoratedPumpkinBlockDataGenerator((LitDecoratedPumpkinBlock) ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get()),
        new LitDecoratedPumpkinBlockDataGenerator((LitDecoratedPumpkinBlock) ModBlocks.LIT_DECORATED_PUMPKIN_GREEN.get()),
        new LitDecoratedPumpkinBlockDataGenerator((LitDecoratedPumpkinBlock) ModBlocks.LIT_DECORATED_PUMPKIN_RED.get()),
        new CandleLitDecoratedPumpkinBlockDataGenerator((CandleLitDecoratedPumpkinBlock) ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get())
    );

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(AllHallowsSteveModelGenerator::new);
        pack.addProvider(AllHallowsSteveBlockLootTables::new);
        pack.addProvider(AllHallowsSteveRecipeProvider::new);
        pack.addProvider(AllHallowsSteveEnglishLangProvider::new);
        pack.addProvider(AllHallowsSteveBlockTagGenerator::new);
        pack.addProvider(AllHallowsSteveItemTagGenerator::new);
        pack.addProvider(AllHallowsSteveEntityTypeTagGenerator::new);
        pack.addProvider(AllHallowsSteveAdvancements::new);
        pack.addProvider(AllHallowsSteveVillagerTradeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.VILLAGER_TRADE, ModVillagerTrades::bootstrap);
    }

    private static class AllHallowsSteveVillagerTradeProvider extends FabricDynamicRegistryProvider {
        AllHallowsSteveVillagerTradeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.add(ModVillagerTrades.FARMER_EMERALD_NUTMEG, registries.lookupOrThrow(Registries.VILLAGER_TRADE).getOrThrow(ModVillagerTrades.FARMER_EMERALD_NUTMEG).value());
        }

        @Override
        public @NotNull String getName() {
            return "AllHallowsSteveVillagerTrades";
        }
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

            PumpkinStencilItemDataGenerator.configureTranslations(registryLookup, translationBuilder);
            CiderItemDataGenerator.configureTranslations(registryLookup, translationBuilder);
            SpiceItemDataGenerator.configureTranslations(registryLookup, translationBuilder);

            translationBuilder.add(CarvingStationScreenHandler.SCREEN_ID, "Pumpkin Carving Station");
            translationBuilder.add(CiderPressScreenHandler.SCREEN_ID, "Cider Press");

            translationBuilder.add("stat.allhallowssteve.light_decorated_pumpkin", "Lit up decorated pumpkin");

            translationBuilder.add("key.allhallowssteve.cycle_pumpkin_face", "Turn Worn Pumpkin");
            translationBuilder.add("key.category.allhallowssteve.keybinds", "All Hallows Steve");

            translationBuilder.add("text.config.allhallowssteve.title", "All Hallows Steve Config");
            translationBuilder.add("text.config.allhallowssteve.option.pumpkinOverlayOpacity", "Worn Pumpkin Vision Overlay");
            translationBuilder.add("text.config.allhallowssteve.option.pumpkinOverlayOpacity.description", "How much a worn decorated pumpkin blocks your view. At 100% an uncarved pumpkin hides everything; lower it to see through.");

            AllHallowsSteveAdvancements.configureTranslations(translationBuilder);

            translationBuilder.add(PumpkinContents.EMPTY_KEY, "Empty");
            translationBuilder.add(PumpkinContents.CANDLE_LIT_KEY, "%s candle (lit)");
            translationBuilder.add(PumpkinContents.CANDLES_LIT_KEY, "%s candles (lit)");
            translationBuilder.add(PumpkinContents.CANDLE_SNUFFED_KEY, "%s candle (snuffed)");
            translationBuilder.add(PumpkinContents.CANDLES_SNUFFED_KEY, "%s candles (snuffed)");

            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.color", "Color: %s");
            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.stencil", "%s: %s");
            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.side.north", "North");
            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.side.east", "East");
            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.side.south", "South");
            translationBuilder.add("item.allhallowssteve.decorated_pumpkin.tooltip.side.west", "West");
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

                    PumpkinStencilItemDataGenerator.configureRecipes(registryLookup, exporter, this);
                    SpiceItemDataGenerator.configureRecipes(registryLookup, exporter, this);
                }
            };
        }

        @Override
        public @NotNull String getName() {
            return "AllHallowsSteveRecipeProvider";
        }
    }

    private static class AllHallowsSteveBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        public AllHallowsSteveBlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registryLookup) {
            for (FabricBlockDataGenerator blockGenerator : BLOCK_GENERATORS) {
                blockGenerator.configureBlockTags(registryLookup, this::builder);
            }

            var cinnamonStrippables = builder(AHSTags.CINNAMON_STRIPPABLES).setReplace(false);
            for (Block block : SpiceItemDataGenerator.CINNAMON_STRIPPABLES) {
                cinnamonStrippables.add(block.builtInRegistryHolder().key());
            }
        }
    }

    /**
     * The unlit decorated pumpkin disguises its wearer from endermen and creakings, like vanilla's
     * carved pumpkin. The rest are the cider press's tags: what it presses and burns, and what pomace is.
     */
    private static class AllHallowsSteveItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {
        public AllHallowsSteveItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registryLookup) {
            builder(ItemTags.GAZE_DISGUISE_EQUIPMENT)
                .setReplace(false)
                .add(ModBlocks.DECORATED_PUMPKIN.get().asItem().builtInRegistryHolder().key());

            builder(ModTags.CIDER_PRESS_PRESSABLE)
                .setReplace(false)
                .add(Items.APPLE.builtInRegistryHolder().key())
                .add(Items.SWEET_BERRIES.builtInRegistryHolder().key())
                .add(Items.GLOW_BERRIES.builtInRegistryHolder().key());

            builder(ModTags.CIDER_PRESS_FUEL)
                .setReplace(false)
                .add(Items.CHARCOAL.builtInRegistryHolder().key())
                .add(Items.COAL.builtInRegistryHolder().key())
                .add(Items.BLAZE_POWDER.builtInRegistryHolder().key());

            builder(ModTags.FERTILIZERS)
                .setReplace(false)
                .add(ModItems.POMACE.get().builtInRegistryHolder().key());

            builder(ItemTags.PIG_FOOD)
                .setReplace(false)
                .add(ModItems.POMACE.get().builtInRegistryHolder().key());

            var cloveFlowers = builder(AHSTags.CLOVE_FLOWERS).setReplace(false);
            for (Item flower : SpiceItemDataGenerator.CLOVE_FLOWERS) {
                cloveFlowers.add(flower.builtInRegistryHolder().key());
            }
        }
    }

    /** The livestock that heal a little when fed pomace: cows, sheep, pigs, and the living horse-likes. */
    private static class AllHallowsSteveEntityTypeTagGenerator extends FabricTagsProvider<EntityType<?>> {
        public AllHallowsSteveEntityTypeTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.ENTITY_TYPE, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registryLookup) {
            builder(ModTags.POMACE_FEEDABLE)
                .setReplace(false)
                .add(EntityTypes.COW.builtInRegistryHolder().key())
                .add(EntityTypes.MOOSHROOM.builtInRegistryHolder().key())
                .add(EntityTypes.SHEEP.builtInRegistryHolder().key())
                .add(EntityTypes.PIG.builtInRegistryHolder().key())
                .add(EntityTypes.HORSE.builtInRegistryHolder().key())
                .add(EntityTypes.DONKEY.builtInRegistryHolder().key())
                .add(EntityTypes.MULE.builtInRegistryHolder().key());
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

            PumpkinStencilItemDataGenerator.configureItemModels(itemModelGenerator);
            CiderItemDataGenerator.configureItemModels(itemModelGenerator);
            SpiceItemDataGenerator.configureItemModels(itemModelGenerator);
        }

        @Override
        public @NotNull String getName() {
            return "AllHallowsSteveModelGenerator";
        }
    }
}
