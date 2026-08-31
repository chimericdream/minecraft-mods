package com.chimericdream.logallthethings.carpetlog.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class CarpetedBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState host;
    public @Nullable MovingBlockRenderState carpet;
}
