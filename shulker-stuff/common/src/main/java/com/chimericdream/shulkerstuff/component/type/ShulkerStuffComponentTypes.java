package com.chimericdream.shulkerstuff.component.type;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ShulkerStuffComponentTypes {
    public static final RegistrySupplier<DataComponentType<ShulkerStuffDyedColorComponent>> SHULKER_STUFF_DYED_COLOR_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffDyedColorComponent.COMPONENT_ID,
        () -> DataComponentType.<ShulkerStuffDyedColorComponent>builder().persistent(ShulkerStuffDyedColorComponent.CODEC).build()
    );
    public static final RegistrySupplier<DataComponentType<ShulkerStuffHardenedComponent>> SHULKER_STUFF_HARDENED_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffHardenedComponent.COMPONENT_ID,
        () -> DataComponentType.<ShulkerStuffHardenedComponent>builder().persistent(ShulkerStuffHardenedComponent.CODEC).build()
    );
    public static final RegistrySupplier<DataComponentType<ShulkerStuffPlatedComponent>> SHULKER_STUFF_PLATED_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffPlatedComponent.COMPONENT_ID,
        () -> DataComponentType.<ShulkerStuffPlatedComponent>builder().persistent(ShulkerStuffPlatedComponent.CODEC).build()
    );

    public static void init() {
    }
}
