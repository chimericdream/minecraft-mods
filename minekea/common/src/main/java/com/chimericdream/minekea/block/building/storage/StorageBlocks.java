package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.item.NuggetBags;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class StorageBlocks implements ModThingGroup {
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Properties DEFAULT_DYE_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.COLORED_BLOCKS);
    @SuppressWarnings("UnstableApiUsage")
    public static final Item.Properties DEFAULT_STORAGE_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> WHITE_DYE_BLOCK;
    public static final RegistrySupplier<Block> ORANGE_DYE_BLOCK;
    public static final RegistrySupplier<Block> MAGENTA_DYE_BLOCK;
    public static final RegistrySupplier<Block> LIGHT_BLUE_DYE_BLOCK;
    public static final RegistrySupplier<Block> YELLOW_DYE_BLOCK;
    public static final RegistrySupplier<Block> LIME_DYE_BLOCK;
    public static final RegistrySupplier<Block> PINK_DYE_BLOCK;
    public static final RegistrySupplier<Block> GRAY_DYE_BLOCK;
    public static final RegistrySupplier<Block> LIGHT_GRAY_DYE_BLOCK;
    public static final RegistrySupplier<Block> CYAN_DYE_BLOCK;
    public static final RegistrySupplier<Block> PURPLE_DYE_BLOCK;
    public static final RegistrySupplier<Block> BLUE_DYE_BLOCK;
    public static final RegistrySupplier<Block> BROWN_DYE_BLOCK;
    public static final RegistrySupplier<Block> GREEN_DYE_BLOCK;
    public static final RegistrySupplier<Block> RED_DYE_BLOCK;
    public static final RegistrySupplier<Block> BLACK_DYE_BLOCK;

    public static final List<RegistrySupplier<Block>> DYE_BLOCKS;

    public static final RegistrySupplier<Block> APPLE_STORAGE_BLOCK;
    public static final RegistrySupplier<Block> BEETROOT_BLOCK;
    public static final RegistrySupplier<Block> BEETROOT_SEEDS_BLOCK;
    public static final RegistrySupplier<Block> BLAZE_POWDER_BLOCK;
    public static final RegistrySupplier<Block> BLAZE_ROD_BLOCK;
    public static final RegistrySupplier<Block> BLUE_EGG_CRATE_BLOCK;
    public static final RegistrySupplier<Block> BREEZE_ROD_BLOCK;
    public static final RegistrySupplier<Block> BROWN_EGG_CRATE_BLOCK;
    public static final RegistrySupplier<Block> CARROT_BLOCK;
    public static final RegistrySupplier<Block> CHARCOAL_BLOCK;
    public static final RegistrySupplier<Block> CHORUS_FRUIT_BLOCK;
    public static final RegistrySupplier<Block> EGG_CRATE_BLOCK;
    public static final RegistrySupplier<Block> ENDER_PEARL_BLOCK;
    public static final RegistrySupplier<Block> FLINT_BLOCK;
    public static final RegistrySupplier<Block> GOLD_NUGGET_SACK;
    public static final RegistrySupplier<Block> GOLDEN_APPLE_BLOCK;
    public static final RegistrySupplier<Block> IRON_NUGGET_SACK;
    public static final RegistrySupplier<Block> LEATHER_BLOCK;
    public static final RegistrySupplier<Block> MELON_SEEDS_BLOCK;
    public static final RegistrySupplier<Block> NETHER_STAR_BLOCK;
    public static final RegistrySupplier<Block> PHANTOM_MEMBRANE_BLOCK;
    public static final RegistrySupplier<Block> POTATO_BLOCK;
    public static final RegistrySupplier<Block> PUMPKIN_SEEDS_BLOCK;
    public static final RegistrySupplier<Block> STICK_BLOCK;
    public static final RegistrySupplier<Block> SUGAR_BLOCK;
    public static final RegistrySupplier<Block> SUGAR_CANE_BLOCK;
    public static final RegistrySupplier<Block> TOTEM_BLOCK;
    public static final RegistrySupplier<Block> WALLPAPER_BLOCK;
    public static final RegistrySupplier<Block> WHEAT_SEEDS_BLOCK;

    public static final List<RegistrySupplier<Block>> STORAGE_BLOCKS;
    public static final List<RegistrySupplier<Block>> BAGGED_BLOCKS;

    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    static {
        WHITE_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("white"), () -> new DyeBlock("white"), DEFAULT_DYE_BLOCK_SETTINGS);
        LIGHT_GRAY_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("light_gray"), () -> new DyeBlock("light_gray"), DEFAULT_DYE_BLOCK_SETTINGS);
        GRAY_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("gray"), () -> new DyeBlock("gray"), DEFAULT_DYE_BLOCK_SETTINGS);
        BLACK_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("black"), () -> new DyeBlock("black"), DEFAULT_DYE_BLOCK_SETTINGS);
        BROWN_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("brown"), () -> new DyeBlock("brown"), DEFAULT_DYE_BLOCK_SETTINGS);
        RED_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("red"), () -> new DyeBlock("red"), DEFAULT_DYE_BLOCK_SETTINGS);
        ORANGE_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("orange"), () -> new DyeBlock("orange"), DEFAULT_DYE_BLOCK_SETTINGS);
        YELLOW_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("yellow"), () -> new DyeBlock("yellow"), DEFAULT_DYE_BLOCK_SETTINGS);
        LIME_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("lime"), () -> new DyeBlock("lime"), DEFAULT_DYE_BLOCK_SETTINGS);
        GREEN_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("green"), () -> new DyeBlock("green"), DEFAULT_DYE_BLOCK_SETTINGS);
        CYAN_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("cyan"), () -> new DyeBlock("cyan"), DEFAULT_DYE_BLOCK_SETTINGS);
        LIGHT_BLUE_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("light_blue"), () -> new DyeBlock("light_blue"), DEFAULT_DYE_BLOCK_SETTINGS);
        BLUE_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("blue"), () -> new DyeBlock("blue"), DEFAULT_DYE_BLOCK_SETTINGS);
        PURPLE_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("purple"), () -> new DyeBlock("purple"), DEFAULT_DYE_BLOCK_SETTINGS);
        MAGENTA_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("magenta"), () -> new DyeBlock("magenta"), DEFAULT_DYE_BLOCK_SETTINGS);
        PINK_DYE_BLOCK = REGISTRY_HELPER.registerWithItem(DyeBlock.makeId("pink"), () -> new DyeBlock("pink"), DEFAULT_DYE_BLOCK_SETTINGS);

        DYE_BLOCKS = List.of(
            WHITE_DYE_BLOCK,
            LIGHT_GRAY_DYE_BLOCK,
            GRAY_DYE_BLOCK,
            BLACK_DYE_BLOCK,
            BROWN_DYE_BLOCK,
            RED_DYE_BLOCK,
            ORANGE_DYE_BLOCK,
            YELLOW_DYE_BLOCK,
            LIME_DYE_BLOCK,
            GREEN_DYE_BLOCK,
            CYAN_DYE_BLOCK,
            LIGHT_BLUE_DYE_BLOCK,
            BLUE_DYE_BLOCK,
            PURPLE_DYE_BLOCK,
            MAGENTA_DYE_BLOCK,
            PINK_DYE_BLOCK
        );

        APPLE_STORAGE_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("apple"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.MELON).sound(SoundType.WOOD)).item(Items.APPLE).material("apple").materialName("Apple").tool(Tool.HOE), false, ItemStorageBlock.StorageModel.FACING), DEFAULT_STORAGE_BLOCK_SETTINGS);
        BEETROOT_SEEDS_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("beetroot_seeds"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.CROP)).item(Items.BEETROOT_SEEDS).material("beetroot_seeds").materialName("Beetroot Seeds").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        BEETROOT_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("beetroot"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.CROP)).item(Items.BEETROOT).material("beetroot").materialName("Beetroot").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        BLAZE_POWDER_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("blaze_powder"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.STEM)).item(Items.BLAZE_POWDER).material("blaze_powder").materialName("Blaze Powder").tool(Tool.SHOVEL)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        BLAZE_ROD_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("blaze_rod"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.STEM)).item(Items.BLAZE_ROD).material("blaze_rod").materialName("Blaze Rod").tool(Tool.AXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        BREEZE_ROD_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("breeze_rod"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_CORE).sound(SoundType.BASALT)).item(Items.BREEZE_ROD).material("breeze_rod").materialName("Breeze Rod").tool(Tool.PICKAXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        CARROT_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("carrot"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.CROP)).item(Items.CARROT).material("carrot").materialName("Carrot").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        CHARCOAL_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("charcoal"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK).sound(SoundType.TUFF)).item(Items.CHARCOAL).material("charcoal").name("Charcoal Block").tool(Tool.PICKAXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        CHORUS_FRUIT_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("chorus_fruit"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).sound(SoundType.WOOD)).item(Items.CHORUS_FRUIT).material("chorus_fruit").materialName("Chorus Fruit").tool(Tool.AXE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        ENDER_PEARL_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("ender_pearl"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK).sound(SoundType.SHROOMLIGHT)).item(Items.ENDER_PEARL).material("ender_pearl").materialName("Ender Pearl").tool(Tool.PICKAXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        FLINT_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("flint"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).sound(SoundType.STONE)).material("flint").item(Items.FLINT).materialName("Flint").tool(Tool.PICKAXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        GOLD_NUGGET_SACK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("gold_nugget"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).sound(SoundType.METAL)).item(NuggetBags.GOLD_NUGGET_BAG).material("gold_nugget").name("Gold Nugget Sack").tool(Tool.PICKAXE).texture("contents", ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/compressed/currency/gold_nugget_bag")).texture("all", ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/compressed/currency/gold_nugget_bag")), true, ItemStorageBlock.StorageModel.CUSTOM), DEFAULT_STORAGE_BLOCK_SETTINGS);
        GOLDEN_APPLE_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("golden_apple"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.MELON).sound(SoundType.WOOD)).item(Items.GOLDEN_APPLE).material("golden_apple").materialName("Golden Apple").tool(Tool.HOE), false, ItemStorageBlock.StorageModel.FACING), DEFAULT_STORAGE_BLOCK_SETTINGS);
        IRON_NUGGET_SACK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("iron_nugget"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.METAL)).item(NuggetBags.IRON_NUGGET_BAG).material("iron_nugget").name("Iron Nugget Sack").tool(Tool.PICKAXE).texture("contents", ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/compressed/currency/iron_nugget_bag")).texture("all", ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/storage/compressed/currency/iron_nugget_bag")), true, ItemStorageBlock.StorageModel.CUSTOM), DEFAULT_STORAGE_BLOCK_SETTINGS);
        LEATHER_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("leather"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).sound(SoundType.WOOL)).item(Items.LEATHER).material("leather").materialName("Leather").tool(Tool.SHEARS)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        MELON_SEEDS_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("melon_seeds"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.CROP)).item(Items.MELON_SEEDS).material("melon_seeds").materialName("Melon Seeds").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        NETHER_STAR_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("nether_star"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).sound(SoundType.METAL)).item(Items.NETHER_STAR).material("nether_star").materialName("Nether Star").tool(Tool.PICKAXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        PHANTOM_MEMBRANE_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("phantom_membrane"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK).sound(SoundType.NETHER_WART)).item(Items.PHANTOM_MEMBRANE).material("phantom_membrane").materialName("Phantom Membrane").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        POTATO_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("potato"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.CROP)).item(Items.POTATO).material("potato").materialName("Potato").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        PUMPKIN_SEEDS_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("pumpkin_seeds"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.CROP)).item(Items.PUMPKIN_SEEDS).material("pumpkin_seeds").materialName("Pumpkin Seeds").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        STICK_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("stick"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).sound(SoundType.WOOD)).item(Items.STICK).material("stick").materialName("Stick").tool(Tool.AXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        SUGAR_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("sugar"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.SAND)).item(Items.SUGAR).material("sugar").materialName("Sugar").tool(Tool.SHOVEL), true), DEFAULT_STORAGE_BLOCK_SETTINGS);
        SUGAR_CANE_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("sugar_cane"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.GRASS)).item(Items.SUGAR_CANE).material("sugar_cane").materialName("Sugar Cane").tool(Tool.AXE).renderType(BlockConfig.RenderType.TRANSLUCENT)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        TOTEM_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("totem_of_undying"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).sound(SoundType.METAL)).item(Items.TOTEM_OF_UNDYING).material("totem_of_undying").materialName("Totem of Undying").tool(Tool.PICKAXE), false, ItemStorageBlock.StorageModel.AXIS), DEFAULT_STORAGE_BLOCK_SETTINGS);
        WALLPAPER_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("paper"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).sound(SoundType.STEM)).item(Items.PAPER).material("paper").name("Wallpaper").tool(Tool.AXE)), DEFAULT_STORAGE_BLOCK_SETTINGS);
        WHEAT_SEEDS_BLOCK = REGISTRY_HELPER.registerWithItem(ItemStorageBlock.makeId("wheat_seeds"), () -> new ItemStorageBlock(new BlockConfig().settings(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion().sound(SoundType.CROP)).item(Items.WHEAT_SEEDS).material("wheat_seeds").materialName("Wheat Seeds").tool(Tool.HOE), true), DEFAULT_STORAGE_BLOCK_SETTINGS);

        BLUE_EGG_CRATE_BLOCK = REGISTRY_HELPER.registerWithItem(BlueEggCrateBlock.BLOCK_ID, BlueEggCrateBlock::new, DEFAULT_STORAGE_BLOCK_SETTINGS);
        BROWN_EGG_CRATE_BLOCK = REGISTRY_HELPER.registerWithItem(BrownEggCrateBlock.BLOCK_ID, BrownEggCrateBlock::new, DEFAULT_STORAGE_BLOCK_SETTINGS);
        EGG_CRATE_BLOCK = REGISTRY_HELPER.registerWithItem(EggCrateBlock.BLOCK_ID, EggCrateBlock::new, DEFAULT_STORAGE_BLOCK_SETTINGS);

        STORAGE_BLOCKS = List.of(
            APPLE_STORAGE_BLOCK,
            BEETROOT_BLOCK,
            BEETROOT_SEEDS_BLOCK,
            BLAZE_POWDER_BLOCK,
            BLAZE_ROD_BLOCK,
            BREEZE_ROD_BLOCK,
            CARROT_BLOCK,
            CHARCOAL_BLOCK,
            CHORUS_FRUIT_BLOCK,
            ENDER_PEARL_BLOCK,
            FLINT_BLOCK,
            GOLD_NUGGET_SACK,
            GOLDEN_APPLE_BLOCK,
            IRON_NUGGET_SACK,
            LEATHER_BLOCK,
            MELON_SEEDS_BLOCK,
            NETHER_STAR_BLOCK,
            PHANTOM_MEMBRANE_BLOCK,
            POTATO_BLOCK,
            PUMPKIN_SEEDS_BLOCK,
            STICK_BLOCK,
            SUGAR_BLOCK,
            SUGAR_CANE_BLOCK,
            TOTEM_BLOCK,
            WALLPAPER_BLOCK,
            WHEAT_SEEDS_BLOCK
        );

        BAGGED_BLOCKS = List.of(
            BEETROOT_BLOCK,
            BEETROOT_SEEDS_BLOCK,
            CARROT_BLOCK,
            CHORUS_FRUIT_BLOCK,
            GOLD_NUGGET_SACK,
            IRON_NUGGET_SACK,
            MELON_SEEDS_BLOCK,
            PHANTOM_MEMBRANE_BLOCK,
            POTATO_BLOCK,
            PUMPKIN_SEEDS_BLOCK,
            SUGAR_BLOCK,
            WHEAT_SEEDS_BLOCK
        );

        BLOCKS.addAll(DYE_BLOCKS);
        BLOCKS.addAll(STORAGE_BLOCKS);
        BLOCKS.add(BLUE_EGG_CRATE_BLOCK);
        BLOCKS.add(BROWN_EGG_CRATE_BLOCK);
        BLOCKS.add(EGG_CRATE_BLOCK);
    }

//    @Environment(EnvType.CLIENT)
//    @Override
//    public void initializeClient() {
//        DYE_BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent()));
//        BAGGED_BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()));
//
//        BlockRenderLayerMap.INSTANCE.putBlock(SET_OF_EGGS_BLOCK, RenderLayer.getCutout());
//        BlockRenderLayerMap.INSTANCE.putBlock(SUGAR_CANE_BLOCK, RenderLayer.getTranslucent());
//    }

//    @Override
//    public void registerBlocks() {
//        MinekeaBlockCategory.super.registerBlocks();
//
//        GOLD_NUGGET_BAG.register();
//        IRON_NUGGET_BAG.register();
//    }
//
//    @Override
//    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, FabricTagProvider<Item>.FabricTagBuilder> getBuilder) {
//        MinekeaBlockCategory.super.configureItemTags(registryLookup, getBuilder);
//
//        GOLD_NUGGET_BAG.configureItemTags(registryLookup, getBuilder);
//        IRON_NUGGET_BAG.configureItemTags(registryLookup, getBuilder);
//    }
//
//    @Override
//    public void configureRecipes(RecipeExporter exporter) {
//        MinekeaBlockCategory.super.configureRecipes(exporter);
//
//        GOLD_NUGGET_BAG.configureRecipes(exporter);
//        IRON_NUGGET_BAG.configureRecipes(exporter);
//    }
//
//    @Override
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        MinekeaBlockCategory.super.configureTranslations(registryLookup, translationBuilder);
//
//        GOLD_NUGGET_BAG.configureTranslations(registryLookup, translationBuilder);
//        IRON_NUGGET_BAG.configureTranslations(registryLookup, translationBuilder);
//    }
//
//    @Override
//    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
//        MinekeaBlockCategory.super.configureItemModels(itemModelGenerator);
//
//        GOLD_NUGGET_BAG.configureItemModels(itemModelGenerator);
//        IRON_NUGGET_BAG.configureItemModels(itemModelGenerator);
//    }
}
