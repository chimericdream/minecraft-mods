package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class GlassJarBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;

    public final ItemStackRenderState displayItem = new ItemStackRenderState();
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
