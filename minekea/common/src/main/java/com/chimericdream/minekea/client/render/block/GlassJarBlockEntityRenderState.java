package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class GlassJarBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemRenderState displayItem = new ItemRenderState();
    public boolean hasItem = false;
    public int fillLevel = 0;

    public boolean hasFluid = false;
    public Fluid storedFluid = Fluids.EMPTY;
    public double fluidAmountInBuckets = 0.0;
}
