package com.chimericdream.shulkerstuff.component.type;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ShulkerStuffComponentTypes {
    public static final RegistrySupplier<ComponentType<ShulkerStuffDyedColorComponent>> SHULKER_STUFF_DYED_COLOR_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffDyedColorComponent.COMPONENT_ID,
        () -> ComponentType.<ShulkerStuffDyedColorComponent>builder().codec(ShulkerStuffDyedColorComponent.CODEC).build()
    );
    public static final RegistrySupplier<ComponentType<ShulkerStuffHardenedComponent>> SHULKER_STUFF_HARDENED_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffHardenedComponent.COMPONENT_ID,
        () -> ComponentType.<ShulkerStuffHardenedComponent>builder().codec(ShulkerStuffHardenedComponent.CODEC).build()
    );
    public static final RegistrySupplier<ComponentType<ShulkerStuffPlatedComponent>> SHULKER_STUFF_PLATED_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffPlatedComponent.COMPONENT_ID,
        () -> ComponentType.<ShulkerStuffPlatedComponent>builder().codec(ShulkerStuffPlatedComponent.CODEC).build()
    );

    public static void init() {
    }
}
