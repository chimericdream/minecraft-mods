package com.chimericdream.bettertargetdummies.client.screen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

import static com.chimericdream.bettertargetdummies.BetterTargetDummiesMod.REGISTRY_HELPER;

public class ModMenus {
    public static final RegistrySupplier<MenuType<MobPickerMenu>> MOB_PICKER_MENU = REGISTRY_HELPER.registerScreenHandler(
        MobPickerMenu.SCREEN_ID,
        () -> new MenuType<>(MobPickerMenu::new, FeatureFlagSet.of())
    );

    public static void init() {
    }
}
