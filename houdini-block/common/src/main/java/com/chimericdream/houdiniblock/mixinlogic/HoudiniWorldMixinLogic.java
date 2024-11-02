package com.chimericdream.houdiniblock.mixinlogic;

import com.chimericdream.houdiniblock.HoudiniBlockMod;
import com.chimericdream.houdiniblock.blocks.HoudiniBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;

public class HoudiniWorldMixinLogic {
    public static boolean preventBlockUpdates(BlockState previousState, BlockState newState, Block newBlock) {
        try {
            if ((
                previousState.getBlock() instanceof HoudiniBlock
                    && !previousState.get(HoudiniBlock.PREVENT_ON_PLACE) // All states except prevent_on_place should prevent updates
                    && (newBlock instanceof AirBlock || newBlock instanceof FluidBlock))
                || (
                previousState.getBlock() instanceof HoudiniBlock
                    && previousState.get(HoudiniBlock.REPLACE_BLOCK))
            ) {
                return true;
            }

            if ((
                (previousState.getBlock() instanceof AirBlock || previousState.getBlock() instanceof FluidBlock)
                    && newBlock instanceof HoudiniBlock
                    // All states except prevent_on_break should prevent updates
                    && !newState.get(HoudiniBlock.PREVENT_ON_BREAK))
                || (
                newBlock instanceof HoudiniBlock
                    && newState.get(HoudiniBlock.REPLACE_BLOCK))
            ) {
                return true;
            }
        } catch (RuntimeException e) {
            HoudiniBlockMod.LOGGER.error("Error in HoudiniWorldMixinLogic$preventBlockUpdates", e);
        }

        return false;
    }
}
