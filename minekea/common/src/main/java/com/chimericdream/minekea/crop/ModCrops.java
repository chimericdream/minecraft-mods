package com.chimericdream.minekea.crop;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.compat.CobbliciousCompat;
import dev.architectury.registry.registries.RegistrySupplier;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ModCrops {
    /**
     * Null when Cobblicious is loaded. Cobblicious registers its own Warped Wart, so Minekea does not.
     */
    public static final @Nullable RegistrySupplier<Block> WARPED_WART_PLANT_BLOCK = CobbliciousCompat.isActive()
        ? null
        : REGISTRY_HELPER.registerBlock(WarpedWartPlantBlock.BLOCK_ID, WarpedWartPlantBlock::new);
    public static final @Nullable RegistrySupplier<Item> WARPED_WART_ITEM = CobbliciousCompat.isActive()
        ? null
        : REGISTRY_HELPER.registerItem(WarpedWartItem.ITEM_ID, WarpedWartItem::new);

    public static void init() {
    }

    public static void postInit() {
        // Cobblicious's Warped Wart still goes in glass jars, as Minekea's did.
        RegistrySupplier<Item> wart = WARPED_WART_ITEM != null ? WARPED_WART_ITEM : CobbliciousCompat.item(WarpedWartItem.ITEM_ID);

        GlassJarBlock.ALLOWED_ITEMS.add(wart.get());
    }
}
