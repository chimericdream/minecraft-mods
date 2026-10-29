package com.chimericdream.forwardingaddress.api;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Adjusts the converted block state for a renamed or merged property value.
 */
@FunctionalInterface
public interface PropertyMapper {
    /**
     * @param oldState the state being replaced
     * @param target   the new block's default state with every property the two blocks share already
     *                 copied across
     * @return the state to place
     */
    BlockState map(BlockState oldState, BlockState target);
}
