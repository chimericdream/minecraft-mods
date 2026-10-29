package com.chimericdream.forwardingaddress.api;

import net.minecraft.world.item.ItemStack;

/**
 * Adjusts a converted item stack, for data components that differ between the old and new item.
 */
@FunctionalInterface
public interface ItemMapper {
    /**
     * @param oldStack  the stack being replaced
     * @param converted the new item with the old stack's count and data components already copied
     * @return the stack to use instead
     */
    ItemStack map(ItemStack oldStack, ItemStack converted);
}
