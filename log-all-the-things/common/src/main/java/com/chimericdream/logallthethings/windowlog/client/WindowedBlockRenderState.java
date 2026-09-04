package com.chimericdream.logallthethings.windowlog.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.CardinalLighting;
import org.jetbrains.annotations.Nullable;

public class WindowedBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState host;
    public @Nullable MovingBlockRenderState window;
    /** Per-neighbor-direction packed light for {@link WindowFrameRenderer}'s frame faces — see {@link com.chimericdream.logallthethings.client.FaceLighting}. */
    public int[] faceLight;
    public CardinalLighting cardinalLighting;
}
