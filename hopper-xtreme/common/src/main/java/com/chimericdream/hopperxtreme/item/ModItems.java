package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.HopperXtremeMod;
import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.screen.HopperItemFilterScreenHandler;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class ModItems {
    public static RegistrySupplier<Item> WRENCH = null;
    public static final RegistrySupplier<Item> HOPPER_ITEM_FILTER_ITEM = REGISTRY_HELPER.registerItem(HopperItemFilterItem.ITEM_ID, HopperItemFilterItem::new);

    public static final RegistrySupplier<MenuType<HopperItemFilterScreenHandler>> HOPPER_ITEM_FILTER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(HopperItemFilterScreenHandler.SCREEN_ID, () -> new MenuType<>(HopperItemFilterScreenHandler::new, FeatureFlagSet.of()));

    public static void init() {
        HopperXtremeMod.LOGGER.info("Checking if Minekea is loaded...");

        if (Platform.isModLoaded("minekea")) {
            HopperXtremeMod.LOGGER.info("Minekea is loaded! Skipping registration of Hopper X-Treme's wrench.");
        } else {
            HopperXtremeMod.LOGGER.info("Minekea not loaded. Registering Hopper X-Treme's wrench.");
            WRENCH = REGISTRY_HELPER.registerItem(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/wrench"), WrenchItem::new);
        }
    }
}
