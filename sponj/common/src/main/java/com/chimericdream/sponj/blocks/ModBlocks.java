package com.chimericdream.sponj.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import java.util.List;

import static com.chimericdream.sponj.SponjMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        SponjBlock.BLOCK_ID,
        SponjBlock::new,
        getDefaultItemSettings().registryKey(SponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> WET_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        WetSponjBlock.BLOCK_ID,
        WetSponjBlock::new,
        getDefaultItemSettings().registryKey(WetSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> LAVA_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        LavaSponjBlock.BLOCK_ID,
        LavaSponjBlock::new,
        getDefaultItemSettings().registryKey(LavaSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> WET_LAVA_SPONJ_BLOCK = REGISTRY_HELPER.registerWithItem(
        WetLavaSponjBlock.BLOCK_ID,
        WetLavaSponjBlock::new,
        getDefaultItemSettings().registryKey(WetLavaSponjBlock.ITEM_REGISTRY_KEY)
        // Disabled until I can figure out how this works on NeoForge
        // getDefaultItemSettings().recipeRemainder(LAVA_SPONJ_BLOCK.get().asItem()).registryKey(WetLavaSponjBlock.ITEM_REGISTRY_KEY)
    );

    public static final List<RegistrySupplier<Block>> SPONJ_BLOCKS = List.of(
        SPONJ_BLOCK,
        WET_SPONJ_BLOCK
    );

    public static final List<RegistrySupplier<Block>> LAVA_SPONJ_BLOCKS = List.of(
        LAVA_SPONJ_BLOCK,
        WET_LAVA_SPONJ_BLOCK
    );

    public static void init() {
        // Disabled until I can figure out cross-loader fuel registries as well as how to have a recipe remainder in the furnace with a stackable item
        // // Wet lava sponjes can smelt 128 items!
        // FuelRegistry.register(25600, WET_LAVA_SPONJ_BLOCK.get());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static Item.Settings getDefaultItemSettings() {
        return new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL).useBlockPrefixedTranslationKey();
    }

    public static List<Block> getSponjBlocks() {
        return SPONJ_BLOCKS.stream().map(RegistrySupplier::get).toList();
    }

    public static List<Block> getLavaSponjBlocks() {
        return LAVA_SPONJ_BLOCKS.stream().map(RegistrySupplier::get).toList();
    }
}
