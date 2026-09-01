package com.chimericdream.logallthethings.snowlog.client;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SnowedBlockRenderState extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState host;
    /** Only populated for the non-stairs render path - see {@code SnowedBlockEntityRenderer#submit}. */
    public @Nullable MovingBlockRenderState snow;
    /** The raw snow state, needed even on the stairs path (which has no {@link #snow} render state). */
    public BlockState snowState = Blocks.AIR.defaultBlockState();
    /** Per-neighbor-direction packed light for {@link SnowStairsRenderer}'s overlay faces — see {@link com.chimericdream.logallthethings.client.FaceLighting}. */
    public int[] faceLight;
    public CardinalLighting cardinalLighting;
}
