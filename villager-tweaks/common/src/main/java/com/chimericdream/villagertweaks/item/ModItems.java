package com.chimericdream.villagertweaks.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.villagertweaks.VillagerTweaksMod.REGISTRY_HELPER;

public class ModItems {
    private static final Item.Properties DEFAULT_SETTINGS = new Item.Properties().stacksTo(1).arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    public static final RegistrySupplier<Item> BAGGED_VILLAGER_ITEM = REGISTRY_HELPER.registerItem(
        BaggedVillagerItem.ITEM_ID,
        () -> new BaggedVillagerItem(DEFAULT_SETTINGS.setId(REGISTRY_HELPER.makeItemRegistryKey(BaggedVillagerItem.ITEM_ID)))
    );

    public static void init() {
    }
}
