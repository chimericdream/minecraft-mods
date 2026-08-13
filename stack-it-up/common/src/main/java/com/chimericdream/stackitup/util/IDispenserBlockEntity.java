package com.chimericdream.stackitup.util;

import net.minecraft.world.item.ItemStack;

public interface IDispenserBlockEntity {
    boolean tryInsertAndStackItem(ItemStack itemStack);
}
