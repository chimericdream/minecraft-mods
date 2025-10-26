package com.chimericdream.archaeologytweaks.client.render.block.entity.state;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ATBrushableBlockEntityRenderState extends BlockEntityRenderState {
    public ItemRenderState itemRenderState = new ItemRenderState();
    public int dusted;
    @Nullable
    public Direction face;
}