package com.chimericdream.shulkerstuff.item;

import com.chimericdream.shulkerstuff.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ModItems {
    public static final RegistrySupplier<Item> PLATED_SHULKER_UPGRADE = REGISTRY_HELPER.registerItem(
        Identifier.of(ModInfo.MOD_ID, "plated_shulker_upgrade_smithing_template"),
        () -> new Item(new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL))
    );

    public static final RegistrySupplier<Item> HARDENED_SHULKER_UPGRADE = REGISTRY_HELPER.registerItem(
        Identifier.of(ModInfo.MOD_ID, "hardened_shulker_upgrade_smithing_template"),
        () -> new Item(new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL))
    );

    public static void init() {
    }
}
