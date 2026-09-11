package com.chimericdream.allhallowssteve.component.type;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class AllHallowsSteveComponentTypes {
    public static final RegistrySupplier<DataComponentType<DyedColorComponent>> DYED_COLOR_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        DyedColorComponent.COMPONENT_ID,
        () -> DataComponentType.<DyedColorComponent>builder().persistent(DyedColorComponent.CODEC).build()
    );

    public static final RegistrySupplier<DataComponentType<PumpkinStencilsComponent>> STENCILS_COMPONENT = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        PumpkinStencilsComponent.COMPONENT_ID,
        () -> DataComponentType.<PumpkinStencilsComponent>builder().persistent(PumpkinStencilsComponent.CODEC).build()
    );

    public static void init() {
    }
}
