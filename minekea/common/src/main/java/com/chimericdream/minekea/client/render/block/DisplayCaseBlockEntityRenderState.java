package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DisplayCaseBlockEntityRenderState extends BlockEntityRenderState {
    private final ItemRenderState displayItem = new ItemRenderState();
    private boolean hasItem = false;
    private boolean isBlock = false;
    private int rotation;
    private Identifier itemId;

    public void clear() {
        displayItem.clear();
        hasItem = false;
        isBlock = false;
        rotation = 0;
        itemId = Registries.ITEM.getId(Items.AIR);
    }

    public ItemRenderState getDisplayItem() {
        return displayItem;
    }

    public boolean hasItem() {
        return hasItem;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setIsBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }

    public Identifier getItem() {
        return itemId;
    }

    public void setItem(ItemStack stack) {
        this.hasItem = !stack.isEmpty();
        this.itemId = Registries.ITEM.getId(stack.getItem());
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
