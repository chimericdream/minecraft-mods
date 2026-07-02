package com.chimericdream.houdiniblock.mixinlogic;

import com.chimericdream.houdiniblock.HoudiniBlockMod;
import com.chimericdream.houdiniblock.blocks.HoudiniBlock;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HoudiniWorldMixinLogic {
    public static boolean preventBlockUpdates(BlockState previousState, BlockState newState, Block newBlock) {
        try {
            if ((
                previousState.getBlock() instanceof HoudiniBlock
                    && !previousState.getValue(HoudiniBlock.PREVENT_ON_PLACE) // All states except prevent_on_place should prevent updates
                    && (newBlock instanceof AirBlock || newBlock instanceof LiquidBlock))
                || (
                previousState.getBlock() instanceof HoudiniBlock
                    && previousState.getValue(HoudiniBlock.REPLACE_BLOCK))
            ) {
                return true;
            }

            if ((
                (previousState.getBlock() instanceof AirBlock || previousState.getBlock() instanceof LiquidBlock)
                    && newBlock instanceof HoudiniBlock
                    // All states except prevent_on_break should prevent updates
                    && !newState.getValue(HoudiniBlock.PREVENT_ON_BREAK))
                || (
                newBlock instanceof HoudiniBlock
                    && newState.getValue(HoudiniBlock.REPLACE_BLOCK))
            ) {
                return true;
            }
        } catch (RuntimeException e) {
            HoudiniBlockMod.LOGGER.error("Error in HoudiniWorldMixinLogic$preventBlockUpdates", e);
        }

        return false;
    }
}
