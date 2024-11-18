package com.chimericdream.shulkerstuff.component.type;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;

import static com.chimericdream.shulkerstuff.ShulkerStuffMod.REGISTRY_HELPER;

public class ShulkerStuffComponentTypes {
    public static final RegistrySupplier<ComponentType<ShulkerStuffDataComponent>> SHULKER_STUFF_DATA = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        ShulkerStuffDataComponent.COMPONENT_ID,
        () -> ComponentType.<ShulkerStuffDataComponent>builder().codec(ShulkerStuffDataComponent.CODEC).build()
    );

    public static void init() {
    }
}
