package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.GlazedHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.GlazedMultiHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeHupperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHupperBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.REDSTONE);

    public static final RegistrySupplier<Block> HONEYED_HOPPER = REGISTRY_HELPER.registerWithItem("honeyed_hopper", () -> new XtremeHopperBlock(20, "honeyed_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> COPPER_HOPPER = REGISTRY_HELPER.registerWithItem("copper_hopper", () -> new XtremeHopperBlock(8, "copper_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_HOPPER = REGISTRY_HELPER.registerWithItem("golden_hopper", () -> new XtremeHopperBlock(4, "golden_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_HOPPER = REGISTRY_HELPER.registerWithItem("diamond_hopper", () -> new XtremeHopperBlock(2, "diamond_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_HOPPER = REGISTRY_HELPER.registerWithItem("netherite_hopper", () -> new XtremeHopperBlock(1, "netherite_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> GLAZED_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_hopper", () -> new GlazedHopperBlock(8, "glazed_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> HONEY_GLAZED_HOPPER = REGISTRY_HELPER.registerWithItem("honey_glazed_hopper", () -> new GlazedHopperBlock(20, "honey_glazed_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_GOLDEN_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_golden_hopper", () -> new GlazedHopperBlock(4, "glazed_golden_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_DIAMOND_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_diamond_hopper", () -> new GlazedHopperBlock(2, "glazed_diamond_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_NETHERITE_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_netherite_hopper", () -> new GlazedHopperBlock(1, "glazed_netherite_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("multi_hopper", () -> new XtremeMultiHopperBlock(8, "multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("golden_multi_hopper", () -> new XtremeMultiHopperBlock(4, "golden_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("diamond_multi_hopper", () -> new XtremeMultiHopperBlock(2, "diamond_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("netherite_multi_hopper", () -> new XtremeMultiHopperBlock(1, "netherite_multi_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> GLAZED_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_multi_hopper", () -> new GlazedMultiHopperBlock(8, "glazed_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_GOLDEN_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_golden_multi_hopper", () -> new GlazedMultiHopperBlock(4, "glazed_golden_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_DIAMOND_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_diamond_multi_hopper", () -> new GlazedMultiHopperBlock(2, "glazed_diamond_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GLAZED_NETHERITE_MULTI_HOPPER = REGISTRY_HELPER.registerWithItem("glazed_netherite_multi_hopper", () -> new GlazedMultiHopperBlock(1, "glazed_netherite_multi_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> HUPPER = REGISTRY_HELPER.registerWithItem("hupper", () -> new XtremeHupperBlock(8, "hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> HONEYED_HUPPER = REGISTRY_HELPER.registerWithItem("honeyed_hupper", () -> new XtremeHupperBlock(20, "honeyed_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> COPPER_HUPPER = REGISTRY_HELPER.registerWithItem("copper_hupper", () -> new XtremeHupperBlock(8, "copper_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_HUPPER = REGISTRY_HELPER.registerWithItem("golden_hupper", () -> new XtremeHupperBlock(4, "golden_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_HUPPER = REGISTRY_HELPER.registerWithItem("diamond_hupper", () -> new XtremeHupperBlock(2, "diamond_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_HUPPER = REGISTRY_HELPER.registerWithItem("netherite_hupper", () -> new XtremeHupperBlock(1, "netherite_hupper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> MULTI_HUPPER = REGISTRY_HELPER.registerWithItem("multi_hupper", () -> new XtremeMultiHupperBlock(8, "multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HUPPER = REGISTRY_HELPER.registerWithItem("golden_multi_hupper", () -> new XtremeMultiHupperBlock(4, "golden_multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HUPPER = REGISTRY_HELPER.registerWithItem("diamond_multi_hupper", () -> new XtremeMultiHupperBlock(2, "diamond_multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HUPPER = REGISTRY_HELPER.registerWithItem("netherite_multi_hupper", () -> new XtremeMultiHupperBlock(1, "netherite_multi_hupper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<GlazedHopperBlockEntity>> GLAZED_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "glazed_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            GlazedHopperBlockEntity::new,
            GLAZED_HOPPER.get(),
            HONEY_GLAZED_HOPPER.get(),
            GLAZED_GOLDEN_HOPPER.get(),
            GLAZED_DIAMOND_HOPPER.get(),
            GLAZED_NETHERITE_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeHopperBlockEntity>> XTREME_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeHopperBlockEntity::new,
            HONEYED_HOPPER.get(),
            COPPER_HOPPER.get(),
            GOLDEN_HOPPER.get(),
            DIAMOND_HOPPER.get(),
            NETHERITE_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeHupperBlockEntity>> XTREME_HUPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_hupper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeHupperBlockEntity::new,
            HUPPER.get(),
            HONEYED_HUPPER.get(),
            COPPER_HUPPER.get(),
            GOLDEN_HUPPER.get(),
            DIAMOND_HUPPER.get(),
            NETHERITE_HUPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<GlazedMultiHopperBlockEntity>> GLAZED_MULTI_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "glazed_multi_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            GlazedMultiHopperBlockEntity::new,
            GLAZED_MULTI_HOPPER.get(),
            GLAZED_GOLDEN_MULTI_HOPPER.get(),
            GLAZED_DIAMOND_MULTI_HOPPER.get(),
            GLAZED_NETHERITE_MULTI_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHopperBlockEntity>> XTREME_MULTI_HOPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_multi_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeMultiHopperBlockEntity::new,
            MULTI_HOPPER.get(),
            GOLDEN_MULTI_HOPPER.get(),
            DIAMOND_MULTI_HOPPER.get(),
            NETHERITE_MULTI_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHupperBlockEntity>> XTREME_MULTI_HUPPER_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        "xtreme_multi_hupper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeMultiHupperBlockEntity::new,
            MULTI_HUPPER.get(),
            GOLDEN_MULTI_HUPPER.get(),
            DIAMOND_MULTI_HUPPER.get(),
            NETHERITE_MULTI_HUPPER.get()
        ).build(null)
    );

    public static void init() {
    }
}
