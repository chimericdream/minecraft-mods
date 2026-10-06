package com.chimericdream.opus.component;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/** Compiles against 26.2; not yet run in game. Mirrors hopper-xtreme's {@code HopperXtremeComponentTypes}. */
public class OpusComponentTypes {
    public static final RegistrySupplier<DataComponentType<BookIdComponent>> BOOK_ID = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        BookIdComponent.COMPONENT_ID,
        () -> DataComponentType.<BookIdComponent>builder().persistent(BookIdComponent.CODEC).build()
    );

    public static void init() {
    }
}
