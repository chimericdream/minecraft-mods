package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.item.PalePumpkinSeedsItem;
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

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> PALE_CARVED_PUMPKIN_BLOCK = REGISTRY_HELPER.registerWithItem(
        PaleCarvedPumpkinBlock.BLOCK_ID,
        PaleCarvedPumpkinBlock::new,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(PaleCarvedPumpkinBlock.ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> PALE_JACK_O_LANTERN_BLOCK = REGISTRY_HELPER.registerWithItem(
        PaleCarvedPumpkinBlock.JOL_BLOCK_ID,
        PaleCarvedPumpkinBlock::createJackOLantern,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(PaleCarvedPumpkinBlock.JOL_ITEM_REGISTRY_KEY)
    );

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> PALE_PUMPKIN_BLOCK = REGISTRY_HELPER.registerWithItem(
        PalePumpkinBlock.BLOCK_ID,
        PalePumpkinBlock::new,
        new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS).useBlockDescriptionPrefix().setId(PalePumpkinBlock.ITEM_REGISTRY_KEY)
    );

    public static final RegistrySupplier<Block> PALE_PUMPKIN_STEM_BLOCK = REGISTRY_HELPER.registerBlock(
        PalePumpkinStemBlock.BLOCK_ID,
        PalePumpkinStemBlock::new
    );

    public static final RegistrySupplier<Block> ATTACHED_PALE_PUMPKIN_STEM_BLOCK = REGISTRY_HELPER.registerBlock(
        AttachedPalePumpkinStemBlock.BLOCK_ID,
        AttachedPalePumpkinStemBlock::new
    );

    public static final RegistrySupplier<Item> PALE_PUMPKIN_SEEDS_ITEM = REGISTRY_HELPER.registerItem(
        PalePumpkinSeedsItem.ITEM_ID,
        PalePumpkinSeedsItem::new
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
