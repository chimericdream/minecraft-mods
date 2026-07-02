package com.chimericdream.minekea.crop;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WarpedWartItem extends BlockItem {
    public static final ResourceLocation ITEM_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "crops/warped_wart");

    public WarpedWartItem() {
        super(ModCrops.WARPED_WART_PLANT_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID)));
    }
}
