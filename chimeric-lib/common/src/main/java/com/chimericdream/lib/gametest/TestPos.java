package com.chimericdream.lib.gametest;

import net.minecraft.util.math.BlockPos;

public class TestPos {
    /*
     * Block positions within a game test structure are relative to the
     * structure block, which is one block below the bounding box of the
     * test structure itself. With this helper method, I can simply use the
     * coordinates of a block as defined in the structure snbt file, without
     * having to worry about or remember the offset.
     */
    public static BlockPos of(int x, int y, int z) {
        return new BlockPos(x, y, z).add(0, 1, 0);
    }
}
