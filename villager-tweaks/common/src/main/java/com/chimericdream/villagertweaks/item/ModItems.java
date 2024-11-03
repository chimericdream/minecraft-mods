package com.chimericdream.villagertweaks.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.chimericdream.villagertweaks.registry.ModRegistries.registerItem;

public class ModItems {
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().maxCount(1).arch$tab(ItemGroups.FUNCTIONAL);

    public static final RegistrySupplier<Item> BAGGED_VILLAGER_ITEM = registerItem(BaggedVillagerItem.ITEM_ID, () -> new BaggedVillagerItem(DEFAULT_SETTINGS));

    public static void init() {
    }
}
