package com.chimericdream.hopperxtreme.component;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class HopperXtremeComponentTypes {
    public static final RegistrySupplier<DataComponentType<HopperXtremeFilterModeComponent>> HOPPER_XTREME_FILTER_MODE_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        HopperXtremeFilterModeComponent.COMPONENT_ID,
        () -> DataComponentType.<HopperXtremeFilterModeComponent>builder().persistent(HopperXtremeFilterModeComponent.CODEC).build()
    );

    public static void init() {
    }
}
