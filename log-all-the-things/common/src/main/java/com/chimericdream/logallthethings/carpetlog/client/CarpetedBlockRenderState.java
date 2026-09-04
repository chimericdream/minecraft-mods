package com.chimericdream.logallthethings.carpetlog.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.CardinalLighting;
import org.jetbrains.annotations.Nullable;

public class CarpetedBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState host;
    public @Nullable MovingBlockRenderState carpet;
    /** Per-neighbor-direction packed light for {@link CarpetFrameRenderer}'s overlay faces — see {@link com.chimericdream.logallthethings.client.FaceLighting}. */
    public int[] faceLight;
    public CardinalLighting cardinalLighting;
}
