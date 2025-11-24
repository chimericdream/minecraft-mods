package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class ShelfBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemRenderState[] displayItems = {
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState()
    };
    public Identifier[] itemIds = {null, null, null, null};
    public boolean[] isBlockItem = {false, false, false, false};
    public boolean[] isJarItem = {false, false, false, false};
    public Direction wallSide = null;

    public void clear() {
        Arrays.stream(displayItems).forEach(ItemRenderState::clear);

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

        this.itemIds[slot] = Registries.ITEM.getId(stack.getItem());
        this.isBlockItem[slot] = isBlockItem;
        this.isJarItem[slot] = isJarItem;
    }
}
