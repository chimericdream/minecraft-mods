package com.chimericdream.logallthethings.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * A plain {@link MovingBlockRenderState} answers every neighboring position with air (see its
 * {@code getBlockState}) — correct for vanilla's own use of this class, a piston's briefly-animating
 * pushed block that really is detached from its neighbors mid-swing, but wrong for
 * {@code windowlog}/{@code carpetlog}, which submit a permanently-placed host/overlay block this way
 * <em>every frame</em>. {@code BlockModelLighter}'s ambient occlusion pass (see
 * {@code MovingBlockFeatureRenderer#buildGroup}, which hands the {@code MovingBlockRenderState} itself
 * to {@code ModelBlockRenderer} as the neighbor-query source) queries each neighbor's
 * {@code BlockState#getShadeBrightness} to darken corners near solid blocks — reporting every neighbor
 * as air means that darkening never happens, so the block renders flatter/brighter than the same block
 * placed normally nearby. Most visible under an overhang (leaves, etc.) that would otherwise cast
 * visible AO shading.
 *
 * <p>Delegating neighbor queries to the real level - substituting only this render's own
 * {@code blockPos} - restores normal ambient occlusion. {@code getHeight()}/{@code getMinY()} don't
 * need overriding: {@code BlockModelLighter}'s AO computation never consults them, only
 * {@code getBlockState}/{@code getFluidState}.
 */
public class RealNeighborMovingBlockRenderState extends MovingBlockRenderState {
    private final ClientLevel level;

    public RealNeighborMovingBlockRenderState(ClientLevel level) {
        this.level = level;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return pos.equals(this.blockPos) ? this.blockState : level.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return pos.equals(this.blockPos) ? this.blockState.getFluidState() : level.getFluidState(pos);
    }
}
