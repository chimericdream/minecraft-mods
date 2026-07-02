package com.chimericdream.lib.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemHelpers {
    public static ResourceLocation getIdentifier(ItemStack stack) {
        if (stack.isEmpty()) {
            return BuiltInRegistries.ITEM.getKey(Items.AIR);
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }
}
