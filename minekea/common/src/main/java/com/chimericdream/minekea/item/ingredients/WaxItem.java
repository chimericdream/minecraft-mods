package com.chimericdream.minekea.item.ingredients;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WaxItem extends Item {
    public final ResourceLocation ITEM_ID;
    public final String color;
    public final Item ingredient;

    public WaxItem(String color, Item ingredient) {
        super(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(makeId(color))));

        ITEM_ID = makeId(color);

        this.color = color;
        this.ingredient = ingredient;
    }

    public static ResourceLocation makeId(String color) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("ingredients/wax/%s", color));
    }
}
