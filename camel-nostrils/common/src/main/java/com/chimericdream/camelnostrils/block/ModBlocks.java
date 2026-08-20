package com.chimericdream.camelnostrils.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.camelnostrils.CamelNostrilsMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.BLOCK_ID,
        LivnaBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> CHIPPED_LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.CHIPPED_BLOCK_ID,
        LivnaBlock::createChipped,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.CHIPPED_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> DAMAGED_LIVNA_BLOCK = REGISTRY_HELPER.registerWithItem(
        LivnaBlock.DAMAGED_BLOCK_ID,
        LivnaBlock::createDamaged,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(LivnaBlock.DAMAGED_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> GOLDEN_CACTUS = REGISTRY_HELPER.registerWithItem(
        GoldenCactusBlock.BLOCK_ID,
        GoldenCactusBlock::create,
        new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS).useBlockDescriptionPrefix().setId(GoldenCactusBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> UPSIDE_DOWN_BED = REGISTRY_HELPER.registerBlock(
        UpsideDownBedBlock.BLOCK_ID,
        UpsideDownBedBlock::create
    );

    static {
        REGISTRY_HELPER.registerItem(
            UpsideDownBedBlock.BLOCK_ID,
            () -> new BedItem(
                UPSIDE_DOWN_BED.get(),
                new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).useBlockDescriptionPrefix().setId(UpsideDownBedBlock.ITEM_REGISTRY_KEY)
            )
        );
    }

    public static void init() {
    }
}
