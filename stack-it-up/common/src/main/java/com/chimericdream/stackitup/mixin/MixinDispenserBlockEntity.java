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
        // The caller treats `true` as "the whole stack was absorbed" and discards it entirely, so
        // this must actually drain itemStack down to empty (spreading across every matching slot
        // with room) rather than stopping after moving a single unit into the first slot found.
        for (int i = 0; i < this.items.size() && !itemStack.isEmpty(); ++i) {
            ItemStack invStack = this.items.get(i);
            if (invStack.getItem() == itemStack.getItem()) {
                int space = invStack.getMaxStackSize() - invStack.getCount();
                if (space > 0) {
                    int transfer = Math.min(space, itemStack.getCount());
                    invStack.grow(transfer);
                    itemStack.shrink(transfer);
                }
            }
        }
        return itemStack.isEmpty();
    }
}
