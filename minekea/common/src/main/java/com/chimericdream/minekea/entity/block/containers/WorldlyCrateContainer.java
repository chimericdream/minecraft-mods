package com.chimericdream.minekea.entity.block.containers;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

/**
 * Exposes a crate's inventory (a single crate, or both halves of a double crate) as a
 * {@link WorldlyContainer} so hoppers, vanilla and Hopper X-Treme alike, see the whole inventory
 * instead of only the half they are touching.
 */
public class WorldlyCrateContainer implements WorldlyContainer {
    private final Container delegate;
    private final int[] slots;

    public WorldlyCrateContainer(Container delegate) {
        this.delegate = delegate;
        this.slots = IntStream.range(0, delegate.getContainerSize()).toArray();
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction side) {
        return delegate.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
        return delegate.canTakeItem(delegate, slot, stack);
    }

    @Override
    public int getContainerSize() {
        return delegate.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return delegate.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        return delegate.removeItem(slot, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return delegate.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        delegate.setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return delegate.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        delegate.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return delegate.stillValid(player);
    }

    @Override
    public void startOpen(@NotNull ContainerUser user) {
        delegate.startOpen(user);
    }

    @Override
    public void stopOpen(@NotNull ContainerUser user) {
        delegate.stopOpen(user);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return delegate.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(@NotNull Container target, int slot, @NotNull ItemStack stack) {
        return delegate.canTakeItem(target, slot, stack);
    }

    @Override
    public void clearContent() {
        delegate.clearContent();
    }
}
