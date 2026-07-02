package com.chimericdream.minekea.client.render.block;

import java.util.Arrays;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ShelfBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState[] displayItems = {
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState()
    };
    public ResourceLocation[] itemIds = {null, null, null, null};
    public boolean[] isBlockItem = {false, false, false, false};
    public boolean[] isJarItem = {false, false, false, false};
    public Direction wallSide = null;

    public void clear() {
        Arrays.stream(displayItems).forEach(ItemStackRenderState::clear);

        for (int i = 0; i < displayItems.length; i += 1) {
            itemIds[i] = null;
            isBlockItem[i] = false;
            isJarItem[i] = false;
        }
    }

    public void setItem(ItemStack stack, boolean isBlockItem, boolean isJarItem, int slot) {
        if (slot < 0 || slot >= displayItems.length) {
            return;
        }

        this.itemIds[slot] = BuiltInRegistries.ITEM.getKey(stack.getItem());
        this.isBlockItem[slot] = isBlockItem;
        this.isJarItem[slot] = isJarItem;
    }
}
