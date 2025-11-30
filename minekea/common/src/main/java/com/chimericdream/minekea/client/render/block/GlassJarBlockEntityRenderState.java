package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

public class GlassJarBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemRenderState displayItem = new ItemRenderState();
    public boolean hasItem = false;
    public int fillLevel = 0;
}
