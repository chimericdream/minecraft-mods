package com.chimericdream.bettertargetdummies.item.component;

import com.chimericdream.bettertargetdummies.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

import static com.chimericdream.bettertargetdummies.BetterTargetDummiesMod.REGISTRY_HELPER;

public class BoundMobComponentTypes {
    public static final Identifier BOUND_MOB_TYPE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "bound_mob_type");

    /**
     * The entity type id (e.g. {@code minecraft:zombie}) picked for a Dummy Spawn Egg via the mob
     * picker screen. Set directly by code, so — unlike the legacy custom-name approach — it never
     * costs the player anvil levels.
     */
    public static final RegistrySupplier<DataComponentType<Identifier>> BOUND_MOB_TYPE = REGISTRY_HELPER.CUSTOM_COMPONENTS.register(
        BOUND_MOB_TYPE_ID,
        () -> DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).build()
    );

    public static void init() {
    }
}
