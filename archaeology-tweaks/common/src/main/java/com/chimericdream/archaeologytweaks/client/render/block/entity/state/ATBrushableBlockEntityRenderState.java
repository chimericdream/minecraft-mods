package com.chimericdream.archaeologytweaks.client.render.block.entity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class ATBrushableBlockEntityRenderState extends BlockEntityRenderState {
    public ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public int dusted;
    @Nullable
    public Direction face;
}