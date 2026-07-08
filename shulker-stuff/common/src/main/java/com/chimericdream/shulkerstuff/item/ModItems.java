package com.chimericdream.shulkerstuff.item;

import com.chimericdream.shulkerstuff.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ModItems {
    public static final ResourceLocation PLATED_ITEM_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "plated_shulker_upgrade_smithing_template");
    public static final ResourceLocation HARDENED_ITEM_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "hardened_shulker_upgrade_smithing_template");

    public static final RegistrySupplier<Item> PLATED_SHULKER_UPGRADE = REGISTRY_HELPER.registerItem(
        PLATED_ITEM_ID,
        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).setId(REGISTRY_HELPER.makeItemRegistryKey(PLATED_ITEM_ID)))
    );

    public static final RegistrySupplier<Item> HARDENED_SHULKER_UPGRADE = REGISTRY_HELPER.registerItem(
        HARDENED_ITEM_ID,
        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).setId(REGISTRY_HELPER.makeItemRegistryKey(HARDENED_ITEM_ID)))
    );

    public static void init() {
    }
}
