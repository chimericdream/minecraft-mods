package com.chimericdream.allhallowssteve.spice;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.tags.AHSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Spices {
    /** Chance that stripping a cherry, mangrove, or jungle log drops a cinnamon. */
    public static final float CINNAMON_STRIP_CHANCE = 0.25f;

    /**
     * Called by {@code AHS$AxeItemMixin} just before an axe replaces {@code state} with its stripped
     * version. Returns whether a cinnamon dropped.
     */
    public static boolean onStrip(ServerLevel level, BlockPos pos, BlockState state) {
        return onStrip(level, pos, state, level.getRandom(), CINNAMON_STRIP_CHANCE);
    }

    public static boolean onStrip(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, float chance) {
        if (!state.is(AHSTags.CINNAMON_STRIPPABLES) || random.nextFloat() >= chance) {
            return false;
        }

        Block.popResource(level, pos, new ItemStack(ModItems.CINNAMON.get()));

        return true;
    }
}
