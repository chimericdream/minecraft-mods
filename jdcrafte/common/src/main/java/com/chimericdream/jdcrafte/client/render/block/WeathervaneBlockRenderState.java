package com.chimericdream.jdcrafte.client.render.block;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class WeathervaneBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState movingBlock;
    public int rotation;
}
