package com.chimericdream.artificialheart.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.artificialheart.ArtificialHeartMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> ARTIFICIAL_CREAKING_HEART_BLOCK = REGISTRY_HELPER.registerWithItem(
        ArtificialCreakingHeartBlock.BLOCK_ID,
        ArtificialCreakingHeartBlock::new,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(ArtificialCreakingHeartBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> CLIPPED_EYEBLOSSOM_BLOCK = REGISTRY_HELPER.registerWithItem(
        ClippedEyeblossomBlock.BLOCK_ID,
        ClippedEyeblossomBlock::new,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(ClippedEyeblossomBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> POTTED_CLIPPED_EYEBLOSSOM_BLOCK = REGISTRY_HELPER.registerWithItem(
        ClippedEyeblossomBlock.POTTED_BLOCK_ID,
        ClippedEyeblossomBlock::getPottedBlock,
        new Item.Properties().useBlockDescriptionPrefix().setId(ClippedEyeblossomBlock.POTTED_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> CLIPPED_OPEN_EYEBLOSSOM_BLOCK = REGISTRY_HELPER.registerWithItem(
        ClippedOpenEyeblossomBlock.BLOCK_ID,
        ClippedOpenEyeblossomBlock::new,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(ClippedOpenEyeblossomBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> POTTED_CLIPPED_OPEN_EYEBLOSSOM_BLOCK = REGISTRY_HELPER.registerWithItem(
        ClippedOpenEyeblossomBlock.POTTED_BLOCK_ID,
        ClippedOpenEyeblossomBlock::getPottedBlock,
        new Item.Properties().useBlockDescriptionPrefix().setId(ClippedOpenEyeblossomBlock.POTTED_ITEM_REGISTRY_KEY)
    );

    public static void init() {
    }
}
