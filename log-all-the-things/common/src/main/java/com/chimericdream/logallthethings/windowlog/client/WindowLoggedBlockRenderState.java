package com.chimericdream.logallthethings.windowlog.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class WindowLoggedBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState host;
    public @Nullable MovingBlockRenderState window;
}
