package com.chimericdream.minekea.data.nbt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class NbtHelpers {
    public static void setCustomDataFromNbt(ItemStack itemStack, CompoundTag nbt) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static CompoundTag getOrCreateNbt(ItemStack stack) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        return component != null ? component.copyTag() : new CompoundTag();
    }
//
//    public static DefaultedList<ItemStack> getInventory(ItemStack stack, int size, RegistryWrapper.WrapperLookup registries) {
//        return getInventory(getOrCreateNbt(stack), size, registries);
//    }
//
//    public static DefaultedList<ItemStack> getInventory(NbtCompound nbt, int size, RegistryWrapper.WrapperLookup registries) {
//        DefaultedList<ItemStack> items = DefaultedList.ofSize(size, ItemStack.EMPTY);
//        Inventories.readNbt(nbt, items, registries);
//
//        return items;
//    }
}
