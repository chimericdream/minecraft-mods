package com.chimericdream.camelnostrils.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.camelnostrils.CamelNostrilsMod.REGISTRY_HELPER;

public class ModItems {
    public static final RegistrySupplier<Item> GOLDEN_WHEAT_SEEDS = REGISTRY_HELPER.registerItem(
        "golden_wheat_seeds",
        () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.FOOD_AND_DRINKS).setId(REGISTRY_HELPER.makeItemRegistryKey("golden_wheat_seeds")))
    );

    public static final RegistrySupplier<Item> GOLDEN_EGG = REGISTRY_HELPER.registerItem(
        "golden_egg",
        () -> new Item(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                .food(Foods.GOLDEN_CARROT)
                .setId(REGISTRY_HELPER.makeItemRegistryKey("golden_egg"))
                .stacksTo(16)
        )
    );

    public static void init() {
    }
}
