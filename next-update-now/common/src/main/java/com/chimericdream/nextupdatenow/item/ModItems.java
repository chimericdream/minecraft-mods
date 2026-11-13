package com.chimericdream.nextupdatenow.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.nextupdatenow.NextUpdateNowMod.REGISTRY_HELPER;

public class ModItems {
    private static final Identifier ICE_BALL_ID = Identifier.withDefaultNamespace("ice_ball");

    public static final RegistrySupplier<Item> ICE_BALL = REGISTRY_HELPER.ITEMS.register(
        ICE_BALL_ID,
        () -> new IceBallItem(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .stacksTo(16)
                .setId(ResourceKey.create(Registries.ITEM, ICE_BALL_ID))
        )
    );

    public static void init() {
        // Loading this class registers the items.
    }
}
