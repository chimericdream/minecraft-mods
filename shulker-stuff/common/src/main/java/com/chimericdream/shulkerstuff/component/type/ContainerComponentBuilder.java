package com.chimericdream.shulkerstuff.component.type;

import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ContainerComponentBuilder {
    private final int size = 27;
    private final DefaultedList<ItemStack> heldStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public ContainerComponentBuilder(ContainerComponent base) {
        if (base.stacks.size() > 27) {
            throw new IllegalArgumentException("Cannot create a ContainerComponentBuilder from a ContainerComponent with more than 27 stacks.");
        }

        for (int i = 0; i < base.stacks.size(); ++i) {
            this.heldStacks.set(i, base.stacks.get(i));
        }
    }


    private int getFirstNonEmptySlot() {
        for (int i = 0; i < this.heldStacks.size(); ++i) {
            if (!this.heldStacks.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private int getMaxAllowed(ItemStack stack) {
        boolean hasEmptySlot = heldStacks.stream().anyMatch(ItemStack::isEmpty);

        if (hasEmptySlot) {
            return stack.getCount();
        }

        int maxAllowed = 0;
        for (ItemStack itemStack : heldStacks) {
            if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
                maxAllowed += stack.getMaxCount() - itemStack.getCount();
            }

            if (maxAllowed >= stack.getCount()) {
                return stack.getCount();
            }
        }

        return maxAllowed;
    }

    public int addFromSlot(Slot slot, PlayerEntity player) {
        ItemStack itemStack = slot.getStack();
        int allowedQty = this.getMaxAllowed(itemStack);

        this.addStack(slot.takeStackRange(itemStack.getCount(), allowedQty, player));

        return allowedQty;
    }

    public void addStack(ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            return;
        }

        ItemStack itemStack = stack.copy();
        this.addToExistingSlot(itemStack);

        if (itemStack.isEmpty()) {
            return;
        }

        this.addToNewSlot(itemStack);
    }

    public ItemStack removeFirst() {
        int i = this.getFirstNonEmptySlot();
        if (i == -1) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = this.heldStacks.get(i).copy();
        this.heldStacks.set(i, ItemStack.EMPTY);

        return itemStack;
    }

    private int getMaxCount(ItemStack stack) {
        return Math.min(99, stack.getMaxCount());
    }

    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack) {
        this.heldStacks.set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
    }

    private void addToNewSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (itemStack.isEmpty()) {
                this.setStack(i, stack.copyAndEmpty());
                return;
            }
        }
    }

    private void addToExistingSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
                this.transfer(stack, itemStack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }
    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = this.getMaxCount(target);
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
        }
    }

    public ContainerComponent build() {
        return ContainerComponent.fromStacks(List.copyOf(this.heldStacks));
    }
}
