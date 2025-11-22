package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DisplayCaseBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemRenderState displayItem = new ItemRenderState();
    public boolean hasItem = false;
    public boolean isBlock = false;
    public int rotation;
    public Identifier itemId;

    public void clear() {
        displayItem.clear();
        hasItem = false;
        isBlock = false;
        rotation = 0;
        itemId = Registries.ITEM.getId(Items.AIR);
    }

    public void setItem(ItemStack stack) {
        this.hasItem = !stack.isEmpty();
        this.itemId = Registries.ITEM.getId(stack.getItem());
    }
}
