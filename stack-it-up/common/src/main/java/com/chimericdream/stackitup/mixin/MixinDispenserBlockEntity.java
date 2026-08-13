package com.chimericdream.stackitup.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.chimericdream.stackitup.util.IDispenserBlockEntity;

@Mixin(DispenserBlockEntity.class)
public class MixinDispenserBlockEntity implements IDispenserBlockEntity {
    @Shadow
    private NonNullList<ItemStack> items;

    @Override
    public boolean tryInsertAndStackItem(ItemStack itemStack) {
        boolean inserted = false;
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack invStack = this.items.get(i);
            if (invStack.getItem() == itemStack.getItem() && invStack.getCount() < invStack.getMaxStackSize()) {
                invStack.grow(1);
                inserted = true;
                break;
            }
        }
        return inserted;
    }
}
