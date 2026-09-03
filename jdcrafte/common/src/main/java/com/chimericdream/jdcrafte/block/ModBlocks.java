package com.chimericdream.jdcrafte.block;

import com.chimericdream.lib.blocks.BlockConfig;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.chimericdream.jdcrafte.JDCrafteMod.REGISTRY_HELPER;

public class ModBlocks {
    public static final RegistrySupplier<Block> FEEDING_TROUGH = REGISTRY_HELPER.registerWithItem(
        "feeding_trough",
        () -> new FeedingTroughBlock(
            BlockBehaviour.Properties
                .of()
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .setId(ResourceKey.create(Registries.BLOCK, REGISTRY_HELPER.makeId("feeding_trough")))
        ),
        new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
    );

    public static final RegistrySupplier<BlockEntityType<FeedingTroughBlockEntity>> FEEDING_TROUGH_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "feeding_trough",
        () -> new BlockEntityType<>(FeedingTroughBlockEntity::new, Set.of(FEEDING_TROUGH.get()))
    );

    public static final RegistrySupplier<Block> WEATHERVANE = REGISTRY_HELPER.registerWithItem(
        "weathervane",
        () -> new WeathervaneBlock(
            BlockBehaviour.Properties
                .of()
                .strength(2.0F)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .setId(ResourceKey.create(Registries.BLOCK, REGISTRY_HELPER.makeId("weathervane")))
        ),
        new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
    );

    public static final RegistrySupplier<BlockEntityType<WeathervaneBlockEntity>> WEATHERVANE_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "weathervane",
        () -> new BlockEntityType<>(WeathervaneBlockEntity::new, Set.of(WEATHERVANE.get()))
    );

    // Shared by TrellisBlock and TrellisArchBlock, which both come in one variant per vanilla wood
    // type. `planks` backs BlockBehaviour.Properties.ofFullCopy in BlockConfig.getBaseSettings():
    // copying properties from the log/stem instead pulls in its per-axis mapColor (reads
    // RotatedPillarBlock.AXIS), which crashes building any BlockState that doesn't have an AXIS
    // property, like these blocks'. `log` is only ever used for its texture (the "log" key), read
    // directly by TrellisBlockDataGenerator/TrellisArchBlockDataGenerator.
    private record WoodType(String material, String materialName, Block planks, Block log, boolean flammable) {
        BlockConfig newConfig() {
            BlockConfig config = new BlockConfig().material(material).materialName(materialName).ingredient(planks).ingredient("log", log);
            if (flammable) {
                config.flammable();
            }

            return config;
        }
    }

    private static final List<WoodType> WOOD_TYPES = List.of(
        new WoodType("acacia", "Acacia", Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, true),
        new WoodType("bamboo", "Bamboo", Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK, true),
        new WoodType("birch", "Birch", Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, true),
        new WoodType("cherry", "Cherry", Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG, true),
        new WoodType("crimson", "Crimson", Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM, false),
        new WoodType("dark_oak", "Dark Oak", Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, true),
        new WoodType("jungle", "Jungle", Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, true),
        new WoodType("mangrove", "Mangrove", Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG, true),
        new WoodType("oak", "Oak", Blocks.OAK_PLANKS, Blocks.OAK_LOG, true),
        new WoodType("pale_oak", "Pale Oak", Blocks.PALE_OAK_PLANKS, Blocks.PALE_OAK_LOG, true),
        new WoodType("spruce", "Spruce", Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, true),
        new WoodType("warped", "Warped", Blocks.WARPED_PLANKS, Blocks.WARPED_STEM, false),

        new WoodType("stripped_acacia", "Stripped Acacia", Blocks.ACACIA_PLANKS, Blocks.STRIPPED_ACACIA_LOG, true),
        new WoodType("stripped_bamboo", "Stripped Bamboo", Blocks.BAMBOO_PLANKS, Blocks.STRIPPED_BAMBOO_BLOCK, true),
        new WoodType("stripped_birch", "Stripped Birch", Blocks.BIRCH_PLANKS, Blocks.STRIPPED_BIRCH_LOG, true),
        new WoodType("stripped_cherry", "Stripped Cherry", Blocks.CHERRY_PLANKS, Blocks.STRIPPED_CHERRY_LOG, true),
        new WoodType("stripped_crimson", "Stripped Crimson", Blocks.CRIMSON_PLANKS, Blocks.STRIPPED_CRIMSON_STEM, false),
        new WoodType("stripped_dark_oak", "Stripped Dark Oak", Blocks.DARK_OAK_PLANKS, Blocks.STRIPPED_DARK_OAK_LOG, true),
        new WoodType("stripped_jungle", "Stripped Jungle", Blocks.JUNGLE_PLANKS, Blocks.STRIPPED_JUNGLE_LOG, true),
        new WoodType("stripped_mangrove", "Stripped Mangrove", Blocks.MANGROVE_PLANKS, Blocks.STRIPPED_MANGROVE_LOG, true),
        new WoodType("stripped_oak", "Stripped Oak", Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG, true),
        new WoodType("stripped_pale_oak", "Stripped Pale Oak", Blocks.PALE_OAK_PLANKS, Blocks.STRIPPED_PALE_OAK_LOG, true),
        new WoodType("stripped_spruce", "Stripped Spruce", Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_SPRUCE_LOG, true),
        new WoodType("stripped_warped", "Stripped Warped", Blocks.WARPED_PLANKS, Blocks.STRIPPED_WARPED_STEM, false)
    );

    public static final Item.Properties DEFAULT_TRELLIS_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);
    public static final List<RegistrySupplier<Block>> TRELLIS_BLOCKS = new ArrayList<>();

    public static final Item.Properties DEFAULT_TRELLIS_ARCH_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);
    public static final List<RegistrySupplier<Block>> TRELLIS_ARCH_BLOCKS = new ArrayList<>();

    static {
        for (WoodType wood : WOOD_TYPES) {
            TRELLIS_BLOCKS.add(REGISTRY_HELPER.registerWithItem(TrellisBlock.makeId(wood.material()), () -> new TrellisBlock(wood.newConfig()), DEFAULT_TRELLIS_SETTINGS));
        }

        for (WoodType wood : WOOD_TYPES) {
            TRELLIS_ARCH_BLOCKS.add(REGISTRY_HELPER.registerWithItem(TrellisArchBlock.makeId(wood.material()), () -> new TrellisArchBlock(wood.newConfig()), DEFAULT_TRELLIS_ARCH_SETTINGS));
        }
    }

    public static void init() {
    }
}
