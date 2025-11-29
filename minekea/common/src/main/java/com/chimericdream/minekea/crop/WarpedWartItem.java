package com.chimericdream.minekea.crop;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WarpedWartItem extends BlockItem {
    public static final Identifier ITEM_ID = Identifier.of(ModInfo.MOD_ID, "crops/warped_wart");

    public WarpedWartItem() {
        super(ModCrops.WARPED_WART_PLANT_BLOCK.get(), new Item.Settings().arch$tab(ItemGroups.INGREDIENTS).registryKey(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID)));
    }
}
