package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;

public class GlassJarBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;

    public final ItemRenderState displayItem = new ItemRenderState();
    public boolean hasItem = false;
    public int fillLevel = 0;

    public boolean hasFluid = false;
    public Fluid storedFluid = Fluids.EMPTY;
    public double fluidAmountInBuckets = 0.0;

    public boolean hasMob = false;
    public EntityRenderState mobDisplay = new EntityRenderState();
    public String mobId = null;
    public float mobHeight = 0.0f;
    public float mobWidth = 0.0f;
}
