package com.chimericdream.hopperxtreme.component;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class HopperXtremeComponentTypes {
    public static final RegistrySupplier<DataComponentType<HopperXtremeFilterModeComponent>> HOPPER_XTREME_FILTER_MODE_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        HopperXtremeFilterModeComponent.COMPONENT_ID,
        () -> DataComponentType.<HopperXtremeFilterModeComponent>builder().persistent(HopperXtremeFilterModeComponent.CODEC).build()
    );

    public static final RegistrySupplier<DataComponentType<HopperXtremeWrenchModeComponent>> HOPPER_XTREME_WRENCH_MODE_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        HopperXtremeWrenchModeComponent.COMPONENT_ID,
        () -> DataComponentType.<HopperXtremeWrenchModeComponent>builder().persistent(HopperXtremeWrenchModeComponent.CODEC).build()
    );

    public static final RegistrySupplier<DataComponentType<HopperXtremeCopiedFilterComponent>> HOPPER_XTREME_COPIED_FILTER_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        HopperXtremeCopiedFilterComponent.COMPONENT_ID,
        () -> DataComponentType.<HopperXtremeCopiedFilterComponent>builder().persistent(HopperXtremeCopiedFilterComponent.CODEC).build()
    );

    public static void init() {
    }
}
