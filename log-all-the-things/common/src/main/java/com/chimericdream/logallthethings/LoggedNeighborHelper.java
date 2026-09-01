package com.chimericdream.logallthethings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.chimericdream.logallthethings.carpetlog.CarpetedBlock;
import com.chimericdream.logallthethings.carpetlog.CarpetedBlockEntity;
import com.chimericdream.logallthethings.snowlog.SnowedBlock;
import com.chimericdream.logallthethings.snowlog.SnowedBlockEntity;

/**
 * The state a wall/fence/bars/pane neighbour-connection check — and a logged block's own
 * {@code updateShape}, reacting to a neighbour change in the other direction — should see at
 * {@code neighborPos}: the real state there, unless it's a {@link CarpetedBlock} or
 * {@link SnowedBlock}, in which case its stored {@code hostState} (the real fence/wall/bars/pane it's
 * actually wrapping) is substituted instead. Neither carrier state carries any of the vanilla
 * connection properties (no {@code NORTH}/{@code EAST}/{@code SOUTH}/{@code WEST}, no
 * {@code CrossCollisionBlock}/{@code WallBlock}/{@code IronBarsBlock} identity), so without this
 * substitution every {@code connectsTo}/{@code attachsTo} check in {@code LATT$FenceBlockMixin},
 * {@code LATT$WallBlockMixin}, and {@code LATT$IronBarsBlockMixin} would see either one as a plain,
 * unrecognized block and refuse to connect.
 *
 * <p>This is deliberately one shared implementation covering both carrier types — not a copy living in
 * each of {@code carpetlog.CarpetLogHelper} and {@code snowlog.SnowLogHelper} — so that a carpet-logged
 * fence connects to a snow-logged fence standing next to it (and vice versa), not just to a neighbour
 * carpet-logged/snow-logged the same way it was. Carpet-logging and snow-logging are mutually exclusive
 * on any single block (see {@code SnowLogHelper}'s class doc), so at most one of the two branches below
 * ever applies for a given neighbour.
 */
public final class LoggedNeighborHelper {
    private LoggedNeighborHelper() {
    }

    public static BlockState effectiveNeighborState(BlockGetter level, BlockPos neighborPos, BlockState neighborState) {
        if (neighborState.getBlock() instanceof CarpetedBlock && level.getBlockEntity(neighborPos) instanceof CarpetedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                return hostState;
            }
        }

        if (neighborState.getBlock() instanceof SnowedBlock && level.getBlockEntity(neighborPos) instanceof SnowedBlockEntity be) {
            BlockState hostState = be.getHostState();
            if (!hostState.isAir()) {
                return hostState;
            }
        }

        return neighborState;
    }
}
