package com.chimericdream.cobblicious.crop;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.cobblicious.CobbliciousMod.REGISTRY_HELPER;

public class ModCrops {
    public static final RegistrySupplier<Block> WARPED_WART_PLANT_BLOCK = REGISTRY_HELPER.registerBlock(WarpedWartPlantBlock.BLOCK_ID, WarpedWartPlantBlock::new);
    public static final RegistrySupplier<Item> WARPED_WART_ITEM = REGISTRY_HELPER.registerItem(WarpedWartItem.ITEM_ID, WarpedWartItem::new);

    public static void init() {
    }
}
