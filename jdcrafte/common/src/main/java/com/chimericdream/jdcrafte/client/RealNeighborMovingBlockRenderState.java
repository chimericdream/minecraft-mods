package com.chimericdream.jdcrafte.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * A plain {@link MovingBlockRenderState} answers every neighboring position with air, which is correct
 * for vanilla's own use of this class (a piston's briefly-detached pushed block) but flattens ambient
 * occlusion for a block that's actually sitting in the world every frame, like the weathervane. See
 * {@code log-all-the-things}'s class of the same name/purpose for the fuller write-up of why this
 * matters and what it fixes.
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
