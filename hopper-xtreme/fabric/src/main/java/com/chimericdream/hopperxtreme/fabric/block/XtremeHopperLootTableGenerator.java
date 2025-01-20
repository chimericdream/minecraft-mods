package com.chimericdream.hopperxtreme.fabric.block;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class XtremeHopperLootTableGenerator extends FabricBlockLootTableProvider {
    public XtremeHopperLootTableGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "X-Treme Hopper loot table generator";
    }

    @Override
    public void generate() {
        generateHopperLootTables();
        generateGlazedHopperLootTables();
        generateMultiHopperLootTables();
        generateHupperLootTables();
        generateMultiHupperLootTables();
    }

    private void generateHopperLootTables() {
        addDrop(ModBlocks.HONEYED_HOPPER.get(), nameableContainerDrops(ModBlocks.HONEYED_HOPPER.get()));
        addDrop(ModBlocks.COPPER_HOPPER.get(), nameableContainerDrops(ModBlocks.COPPER_HOPPER.get()));
        addDrop(ModBlocks.GOLDEN_HOPPER.get(), nameableContainerDrops(ModBlocks.GOLDEN_HOPPER.get()));
        addDrop(ModBlocks.DIAMOND_HOPPER.get(), nameableContainerDrops(ModBlocks.DIAMOND_HOPPER.get()));
        addDrop(ModBlocks.NETHERITE_HOPPER.get(), nameableContainerDrops(ModBlocks.NETHERITE_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GOLDEN_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GOLDEN_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_DIAMOND_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_DIAMOND_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_NETHERITE_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_NETHERITE_HOPPER.get()));
    }

    private void generateGlazedHopperLootTables() {
        addDrop(ModBlocks.GLAZED_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_HOPPER.get()));
        addDrop(ModBlocks.HONEY_GLAZED_HOPPER.get(), nameableContainerDrops(ModBlocks.HONEY_GLAZED_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_GOLDEN_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_GOLDEN_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_DIAMOND_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_DIAMOND_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_NETHERITE_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_NETHERITE_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get()));
    }

    private void generateMultiHopperLootTables() {
        addDrop(ModBlocks.MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.MULTI_HOPPER.get()));
        addDrop(ModBlocks.GOLDEN_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.GOLDEN_MULTI_HOPPER.get()));
        addDrop(ModBlocks.DIAMOND_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.DIAMOND_MULTI_HOPPER.get()));
        addDrop(ModBlocks.NETHERITE_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.NETHERITE_MULTI_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_MULTI_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get()));
        addDrop(ModBlocks.GLAZED_NETHERITE_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.GLAZED_NETHERITE_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_DIAMOND_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_DIAMOND_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_NETHERITE_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_NETHERITE_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_GOLDEN_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_GOLDEN_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_DIAMOND_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_DIAMOND_MULTI_HOPPER.get()));
        addDrop(ModBlocks.FILTERED_GLAZED_NETHERITE_MULTI_HOPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GLAZED_NETHERITE_MULTI_HOPPER.get()));
    }

    private void generateHupperLootTables() {
        addDrop(ModBlocks.HUPPER.get(), nameableContainerDrops(ModBlocks.HUPPER.get()));
        addDrop(ModBlocks.HONEYED_HUPPER.get(), nameableContainerDrops(ModBlocks.HONEYED_HUPPER.get()));
        addDrop(ModBlocks.COPPER_HUPPER.get(), nameableContainerDrops(ModBlocks.COPPER_HUPPER.get()));
        addDrop(ModBlocks.GOLDEN_HUPPER.get(), nameableContainerDrops(ModBlocks.GOLDEN_HUPPER.get()));
        addDrop(ModBlocks.DIAMOND_HUPPER.get(), nameableContainerDrops(ModBlocks.DIAMOND_HUPPER.get()));
        addDrop(ModBlocks.NETHERITE_HUPPER.get(), nameableContainerDrops(ModBlocks.NETHERITE_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_GOLDEN_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GOLDEN_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_DIAMOND_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_DIAMOND_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_NETHERITE_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_NETHERITE_HUPPER.get()));
    }

    private void generateMultiHupperLootTables() {
        addDrop(ModBlocks.MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.MULTI_HUPPER.get()));
        addDrop(ModBlocks.GOLDEN_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.GOLDEN_MULTI_HUPPER.get()));
        addDrop(ModBlocks.DIAMOND_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.DIAMOND_MULTI_HUPPER.get()));
        addDrop(ModBlocks.NETHERITE_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.NETHERITE_MULTI_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_GOLDEN_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_GOLDEN_MULTI_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_DIAMOND_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_DIAMOND_MULTI_HUPPER.get()));
        addDrop(ModBlocks.FILTERED_NETHERITE_MULTI_HUPPER.get(), nameableContainerDrops(ModBlocks.FILTERED_NETHERITE_MULTI_HUPPER.get()));
    }
}
