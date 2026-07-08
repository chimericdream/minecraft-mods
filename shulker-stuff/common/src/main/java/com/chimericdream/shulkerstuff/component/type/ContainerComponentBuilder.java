package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class ContainerComponentBuilder {
    private final int size = 27;
    private final NonNullList<ItemStack> heldStacks = NonNullList.withSize(27, ItemStack.EMPTY);

    public ContainerComponentBuilder(ItemContainerContents base) {
        List<ItemStack> contents = base.nonEmptyStream().toList();
        if (contents.size() > 27) {
            throw new IllegalArgumentException("Cannot create a ContainerComponentBuilder from a ContainerComponent with more than 27 stacks.");
        }

        for (int i = 0; i < contents.size(); ++i) {
            this.heldStacks.set(i, contents.get(i));
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
            if (ItemStack.isSameItemSameComponents(itemStack, stack)) {
                maxAllowed += stack.getMaxStackSize() - itemStack.getCount();
            }

            if (maxAllowed >= stack.getCount()) {
                return stack.getCount();
            }
        }

        return maxAllowed;
    }

    public int addFromSlot(Slot slot, Player player) {
        ItemStack itemStack = slot.getItem();
        int allowedQty = this.getMaxAllowed(itemStack);

        this.addStack(slot.safeTake(itemStack.getCount(), allowedQty, player));

        return allowedQty;
    }

    public void addStackForVacuum(ItemStack stack, int level) {
        if (level == 2) {
            this.addStack(stack);
            return;
        }

        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }

        this.addToExistingSlot(stack);
    }

    public void addStackForVoid(ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }

        int originalCount = stack.getCount();
        this.addToExistingSlot(stack);

        if (originalCount == stack.getCount() && !this.contains(stack)) {
            return;
        }

        stack.shrink(stack.getCount());
    }

    public void addStack(ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }

        this.addToExistingSlot(stack);

        if (stack.isEmpty()) {
            return;
        }

        this.addToNewSlot(stack);
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
        return Math.min(99, stack.getMaxStackSize());
    }

    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack) {
        this.heldStacks.set(slot, stack);
        stack.limitSize(this.getMaxCount(stack));
    }

    public boolean contains(ItemStack stack) {
        return this.heldStacks.stream().anyMatch((itemStack) -> ItemStack.isSameItemSameComponents(itemStack, stack));
    }

    public ItemStack getFirstMatchingStack(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (ItemStack.isSameItemSameComponents(itemStack, stack)) {
                return itemStack.copyAndClear();
            }
        }

        return ItemStack.EMPTY;
    }

    private void addToNewSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (itemStack.isEmpty()) {
                this.setStack(i, stack.copyAndClear());
                return;
            }
        }
    }

    private void addToExistingSlot(ItemStack stack) {
        ShulkerStuffMod.LOGGER.trace("Attempting to add {} {} to existing slots", stack.getCount(), stack.getDisplayName());
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (ItemStack.isSameItemSameComponents(itemStack, stack)) {
                ShulkerStuffMod.LOGGER.trace("{} of {} before adding to to slot {}", stack.getCount(), stack.getDisplayName(), i);
                this.transfer(stack, itemStack);
                ShulkerStuffMod.LOGGER.trace("{} of {} after adding to to slot {}", stack.getCount(), stack.getDisplayName(), i);
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
            target.grow(j);
            source.shrink(j);
        }
    }

    public ItemContainerContents build() {
        return ItemContainerContents.fromItems(List.copyOf(this.heldStacks));
    }
}
