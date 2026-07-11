package com.chimericdream.hopperxtreme.fabric.data;

import com.chimericdream.hopperxtreme.fabric.block.XtremeHopperLootTableGenerator;
import com.chimericdream.hopperxtreme.fabric.block.XtremeHopperRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(XtremeHopperLootTableGenerator::new);
    }

    private static class ModRecipeProvider extends FabricRecipeProvider {
        public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
            return new XtremeHopperRecipeGenerator(registryLookup, exporter);
        }

        @Override
        public @NotNull String getName() {
            return "X-Treme Hopper recipe generator";
        }
    }
}
