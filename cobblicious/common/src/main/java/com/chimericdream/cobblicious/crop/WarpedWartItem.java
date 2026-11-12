package com.chimericdream.cobblicious.crop;

import com.chimericdream.cobblicious.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.cobblicious.CobbliciousMod.REGISTRY_HELPER;

public class WarpedWartItem extends BlockItem {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "warped_wart");

    public WarpedWartItem() {
        super(ModCrops.WARPED_WART_PLANT_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID)));
    }
}
