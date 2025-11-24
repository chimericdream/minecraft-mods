package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class DisplayCaseBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemRenderState displayItem = new ItemRenderState();
    public final Vec3d nameLabelPos = new Vec3d(0.5f, 1.0f, 0.5f);
    public Text customName = null;
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
        itemId = Registries.ITEM.getId(Items.AIR);
        distanceToCamera = 0;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
        this.hasCustomName = true;
    }

    public void setItem(ItemStack stack) {
        this.hasItem = !stack.isEmpty();
        this.itemId = Registries.ITEM.getId(stack.getItem());
    }
}
