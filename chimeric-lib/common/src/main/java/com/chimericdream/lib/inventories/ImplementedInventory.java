package com.chimericdream.lib.inventories;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 * Originally by Juuz
 */
public interface ImplementedInventory extends Container {
    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    NonNullList<ItemStack> getItems();

    /**
     * Creates an inventory from the item list.
     */
    static ImplementedInventory of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new inventory with the specified size.
     */
    static ImplementedInventory ofSize(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    /**
     * Returns the inventory size.
     */
    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    default boolean hasContents() {
        return !isEmpty();
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    default @NonNull ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    default @NonNull ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);

        if (!result.isEmpty()) {
            setChanged();
        }

        return result;
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    @Override
    default @NonNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack items = ContainerHelper.takeItem(getItems(), slot);
        setChanged();

        return items;
    }

    default ItemStack removeStack() {
        return removeItemNoUpdate(0);
    }

    default boolean isMatchingPartialStack(ItemStack incomingStack, ItemStack existingStack) {
        if (!existingStack.isStackable()) {
            return false;
        }

        // Use isSameItemSameComponents, NOT ItemStack.matches: matches also compares counts, so two
        // otherwise-identical partial stacks would only be considered mergeable when their counts
        // happened to be equal. Merging must depend only on item + components.
        if (!ItemStack.isSameItemSameComponents(incomingStack, existingStack)) {
            return false;
        }

        return existingStack.getCount() < existingStack.getMaxStackSize();
    }

    default ItemStack tryInsert(ItemStack stack) {
        return tryInsert(0, stack);
    }

    default ItemStack tryInsert(int slot, ItemStack stack) {
        ItemStack oldStack = getItem(slot);

        if (oldStack.isEmpty()) {
            getItems().set(slot, stack);

            setChanged();

            return ItemStack.EMPTY;
        }

        if (isMatchingPartialStack(stack, oldStack)) {
            int stackDiff = oldStack.getMaxStackSize() - oldStack.getCount();

            // The new stack will completely fit into the slot
            if (stack.getCount() <= stackDiff) {
                oldStack.setCount(oldStack.getCount() + stack.getCount());

                setChanged();

                return ItemStack.EMPTY;
            }

            stack.setCount(stack.getCount() - stackDiff);
            oldStack.setCount(oldStack.getMaxStackSize());
        }

        return stack;
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link Container#getMaxStackSize()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    default void setItem(int slot, @NonNull ItemStack stack) {
        getItems().set(slot, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    /**
     * Clears the inventory.
     * <p>
     * Fills every slot with {@link ItemStack#EMPTY} rather than calling {@code getItems().clear()}:
     * the common backing list is {@code NonNullList.withSize(...)}, whose fixed-size backing makes a
     * structural {@code clear()} either throw or collapse the list to size 0 (breaking the fixed-slot
     * invariant every other method here relies on). Emptying in place keeps the slot count stable.
     */
    @Override
    default void clearContent() {
        NonNullList<ItemStack> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    @Override
    default void setChanged() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    default boolean stillValid(@NonNull Player player) {
        return true;
    }
}
