package com.chimericdream.hopperxtreme.item;

import com.chimericdream.lib.item.AbstractWrenchItem;
import com.chimericdream.hopperxtreme.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class WrenchItem extends AbstractWrenchItem {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/wrench");

    @SuppressWarnings("UnstableApiUsage")
    public WrenchItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }
}
