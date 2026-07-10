package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class DisplayCaseBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState displayItem = new ItemStackRenderState();
    public final Vec3 nameLabelPos = new Vec3(0.5f, 1.0f, 0.5f);
    public Component customName = null;
    public boolean hasCustomName = false;
    public boolean hasItem = false;
    public boolean isBlock = false;
    public int rotation;
    public Identifier itemId;
    public double distanceToCamera = 0;

    public void clear() {
        displayItem.clear();
        customName = null;
        hasCustomName = false;
        hasItem = false;
        isBlock = false;
        rotation = 0;
        itemId = BuiltInRegistries.ITEM.getKey(Items.AIR);
        distanceToCamera = 0;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
        this.hasCustomName = true;
    }

    public void setItem(ItemStack stack) {
        this.hasItem = !stack.isEmpty();
        this.itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
    }
}
