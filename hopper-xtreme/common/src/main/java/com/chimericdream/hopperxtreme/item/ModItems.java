package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.HopperXtremeMod;
import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.screen.DiamondHopperItemFilterScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.HopperItemFilterScreenHandler;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class ModItems {
    public static final RegistrySupplier<Item> WRENCH = REGISTRY_HELPER.registerItem(WrenchItem.ITEM_ID, WrenchItem::new);
    public static final RegistrySupplier<Item> HOPPER_ITEM_FILTER_ITEM = REGISTRY_HELPER.registerItem(
        HopperItemFilterItem.ITEM_ID,
        () -> new HopperItemFilterItem(HopperItemFilterItem.ITEM_ID, HopperItemFilterItem.STANDARD_FILTER_SLOTS, HopperItemFilterScreenHandler::new)
    );
    public static final RegistrySupplier<Item> DIAMOND_HOPPER_ITEM_FILTER_ITEM = REGISTRY_HELPER.registerItem(
        HopperItemFilterItem.DIAMOND_ITEM_ID,
        () -> new HopperItemFilterItem(HopperItemFilterItem.DIAMOND_ITEM_ID, HopperItemFilterItem.DIAMOND_FILTER_SLOTS, DiamondHopperItemFilterScreenHandler::new)
    );

    public static final RegistrySupplier<MenuType<HopperItemFilterScreenHandler>> HOPPER_ITEM_FILTER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(HopperItemFilterScreenHandler.SCREEN_ID, () -> new MenuType<>(HopperItemFilterScreenHandler::new, FeatureFlagSet.of()));
    public static final RegistrySupplier<MenuType<DiamondHopperItemFilterScreenHandler>> DIAMOND_HOPPER_ITEM_FILTER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(DiamondHopperItemFilterScreenHandler.SCREEN_ID, () -> new MenuType<>(DiamondHopperItemFilterScreenHandler::new, FeatureFlagSet.of()));

    public static void init() {
    }
}
