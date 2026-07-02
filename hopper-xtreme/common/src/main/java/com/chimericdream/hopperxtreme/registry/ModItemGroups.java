package com.chimericdream.hopperxtreme.registry;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTab;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class ModItemGroups {
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistrySupplier<CreativeModeTab> HOPPER_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.hopperxtreme.hoppers",
        () -> ModBlocks.DIAMOND_HOPPER.get()
    );

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistrySupplier<CreativeModeTab> MULTI_HOPPER_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.hopperxtreme.multi_hoppers",
        () -> ModBlocks.DIAMOND_MULTI_HOPPER.get()
    );

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistrySupplier<CreativeModeTab> HUPPER_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.hopperxtreme.huppers",
        () -> ModBlocks.GOLDEN_HUPPER.get()
    );

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final RegistrySupplier<CreativeModeTab> MULTI_HUPPER_ITEM_GROUP = REGISTRY_HELPER.registerItemGroup(
        "item_group.hopperxtreme.multi_huppers",
        () -> ModBlocks.GOLDEN_MULTI_HUPPER.get()
    );

    public static void init() {
    }
}
