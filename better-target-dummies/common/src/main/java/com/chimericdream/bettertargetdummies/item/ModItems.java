package com.chimericdream.bettertargetdummies.item;

import com.chimericdream.bettertargetdummies.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.bettertargetdummies.BetterTargetDummiesMod.REGISTRY_HELPER;

public class ModItems {
    public static final Identifier DUMMY_SPAWN_EGG_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dummy_spawn_egg");

    public static final RegistrySupplier<Item> DUMMY_SPAWN_EGG = REGISTRY_HELPER.registerItem(
        DUMMY_SPAWN_EGG_ID,
        () -> new DummySpawnEggItem(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.COMBAT)
                .stacksTo(16)
                .setId(REGISTRY_HELPER.makeItemRegistryKey(DUMMY_SPAWN_EGG_ID))
        )
    );

    public static void init() {
    }
}
