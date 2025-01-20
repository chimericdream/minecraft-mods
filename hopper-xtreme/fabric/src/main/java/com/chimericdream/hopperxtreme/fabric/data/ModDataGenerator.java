package com.chimericdream.hopperxtreme.fabric.data;

import com.chimericdream.hopperxtreme.fabric.block.XtremeHopperLootTableGenerator;
import com.chimericdream.hopperxtreme.fabric.block.XtremeHopperRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(XtremeHopperLootTableGenerator::new);
    }

    private static class ModRecipeProvider extends FabricRecipeProvider {
        public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            XtremeHopperRecipeGenerator generator = new XtremeHopperRecipeGenerator(exporter);
            generator.generate();
        }

        @Override
        public String getName() {
            return "X-Treme Hopper recipe generator";
        }
    }
}
