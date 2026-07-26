package com.chimericdream.minekea.item.tools;

import com.chimericdream.lib.item.AbstractWrenchItem;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class WrenchItem extends AbstractWrenchItem {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/wrench");

    public WrenchItem() {
        super(new Item.Properties().stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES).setId(ResourceKey.create(Registries.ITEM, ITEM_ID)));
    }
}
