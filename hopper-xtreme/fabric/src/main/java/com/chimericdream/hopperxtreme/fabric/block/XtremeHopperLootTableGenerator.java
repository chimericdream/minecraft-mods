package com.chimericdream.hopperxtreme.fabric.block;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class XtremeHopperLootTableGenerator extends FabricBlockLootSubProvider {
    public XtremeHopperLootTableGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public @NotNull String getName() {
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
        add(ModBlocks.HONEYED_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.HONEYED_HOPPER.get()));
        add(ModBlocks.COPPER_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.COPPER_HOPPER.get()));
        add(ModBlocks.GOLDEN_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GOLDEN_HOPPER.get()));
        add(ModBlocks.DIAMOND_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.DIAMOND_HOPPER.get()));
        add(ModBlocks.NETHERITE_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.NETHERITE_HOPPER.get()));
        add(ModBlocks.FILTERED_GOLDEN_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GOLDEN_HOPPER.get()));
        add(ModBlocks.FILTERED_DIAMOND_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_DIAMOND_HOPPER.get()));
        add(ModBlocks.FILTERED_NETHERITE_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_NETHERITE_HOPPER.get()));
    }

    private void generateGlazedHopperLootTables() {
        add(ModBlocks.GLAZED_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_HOPPER.get()));
        add(ModBlocks.HONEY_GLAZED_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.HONEY_GLAZED_HOPPER.get()));
        add(ModBlocks.GLAZED_GOLDEN_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_GOLDEN_HOPPER.get()));
        add(ModBlocks.GLAZED_DIAMOND_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_DIAMOND_HOPPER.get()));
        add(ModBlocks.GLAZED_NETHERITE_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_NETHERITE_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get()));
    }

    private void generateMultiHopperLootTables() {
        add(ModBlocks.MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.MULTI_HOPPER.get()));
        add(ModBlocks.GOLDEN_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GOLDEN_MULTI_HOPPER.get()));
        add(ModBlocks.DIAMOND_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.DIAMOND_MULTI_HOPPER.get()));
        add(ModBlocks.NETHERITE_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.NETHERITE_MULTI_HOPPER.get()));
        add(ModBlocks.GLAZED_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_MULTI_HOPPER.get()));
        add(ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get()));
        add(ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get()));
        add(ModBlocks.GLAZED_NETHERITE_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.GLAZED_NETHERITE_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_DIAMOND_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_DIAMOND_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_NETHERITE_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_NETHERITE_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_GOLDEN_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_GOLDEN_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_DIAMOND_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_DIAMOND_MULTI_HOPPER.get()));
        add(ModBlocks.FILTERED_GLAZED_NETHERITE_MULTI_HOPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GLAZED_NETHERITE_MULTI_HOPPER.get()));
    }

    private void generateHupperLootTables() {
        add(ModBlocks.HUPPER.get(), createNameableBlockEntityTable(ModBlocks.HUPPER.get()));
        add(ModBlocks.HONEYED_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.HONEYED_HUPPER.get()));
        add(ModBlocks.COPPER_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.COPPER_HUPPER.get()));
        add(ModBlocks.GOLDEN_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.GOLDEN_HUPPER.get()));
        add(ModBlocks.DIAMOND_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.DIAMOND_HUPPER.get()));
        add(ModBlocks.NETHERITE_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.NETHERITE_HUPPER.get()));
        add(ModBlocks.FILTERED_GOLDEN_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GOLDEN_HUPPER.get()));
        add(ModBlocks.FILTERED_DIAMOND_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_DIAMOND_HUPPER.get()));
        add(ModBlocks.FILTERED_NETHERITE_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_NETHERITE_HUPPER.get()));
    }

    private void generateMultiHupperLootTables() {
        add(ModBlocks.MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.MULTI_HUPPER.get()));
        add(ModBlocks.GOLDEN_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.GOLDEN_MULTI_HUPPER.get()));
        add(ModBlocks.DIAMOND_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.DIAMOND_MULTI_HUPPER.get()));
        add(ModBlocks.NETHERITE_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.NETHERITE_MULTI_HUPPER.get()));
        add(ModBlocks.FILTERED_GOLDEN_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_GOLDEN_MULTI_HUPPER.get()));
        add(ModBlocks.FILTERED_DIAMOND_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_DIAMOND_MULTI_HUPPER.get()));
        add(ModBlocks.FILTERED_NETHERITE_MULTI_HUPPER.get(), createNameableBlockEntityTable(ModBlocks.FILTERED_NETHERITE_MULTI_HUPPER.get()));
    }
}
