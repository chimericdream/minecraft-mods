package com.chimericdream.betterportallinking.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.BlockUtil;

import java.util.List;
import java.util.function.Predicate;

/**
 * Level-independent frame geometry for a nether portal rectangle.
 *
 * <p>Deliberately free of {@code Level}/{@code ServerLevel} references — callers supply a
 * {@link Predicate} that answers "is this position part of the portal", so this class (and its
 * corner-derivation math) is unit-testable without a running server.
 *
 * <p>{@link #resolve} reuses vanilla's own {@link BlockUtil#getLargestRectangleAround} so the
 * rectangle resolved here is byte-for-byte the same one
 * {@code NetherPortalBlock#getExitPortal}/{@code #getDimensionTransitionFromExit} would compute,
 * given the same predicate.
 */
public final class PortalFrame {
    private final BlockUtil.FoundRectangle rectangle;
    private final Direction.Axis axis;

    private PortalFrame(BlockUtil.FoundRectangle rectangle, Direction.Axis axis) {
        this.rectangle = rectangle;
        this.axis = axis;
    }

    /**
     * Resolves the largest portal rectangle containing {@code start}, exactly as vanilla does:
     * {@code BlockUtil.getLargestRectangleAround(start, axis, 21, Direction.Axis.Y, 21, isPortalBlock)}.
     */
    public static PortalFrame resolve(Predicate<BlockPos> isPortalBlock, BlockPos start, Direction.Axis axis) {
        BlockUtil.FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(
            start, axis, 21, Direction.Axis.Y, 21, isPortalBlock);
        return new PortalFrame(rectangle, axis);
    }

    public BlockUtil.FoundRectangle rectangle() {
        return rectangle;
    }

    public Direction.Axis axis() {
        return axis;
    }

    /**
     * Identity key for this physical portal, used to group/deduplicate POI records that all
     * belong to the same rectangle. Two {@link PortalFrame}s resolved from the same portal always
     * share the same {@code minCorner}.
     */
    public BlockPos identityKey() {
        return rectangle.minCorner;
    }

    /**
     * The four diagonal corner positions just outside the frame's obsidian border — the spots
     * vanilla portal generation leaves untouched, where a player can place address marker blocks.
     *
     * <p>With {@code right = Direction.get(POSITIVE, axis)}, {@code w = axis1Size},
     * {@code h = axis2Size}, and {@code min = rectangle.minCorner} (the interior bottom-left
     * block):
     * <pre>
     * bottomLeft  = min.relative(right, -1).below()
     * bottomRight = min.relative(right,  w).below()
     * topLeft     = min.relative(right, -1).above(h)
     * topRight    = min.relative(right,  w).above(h)
     * </pre>
     *
     * <p>Two same-axis portals in one plane separated by a single frame column resolve to the same
     * position for one portal's right corners and the other's left corners, so a block placed there
     * is read into both addresses. That is intended, not a bug to fix: every candidate's address is
     * built independently from its own corners, so the pair scores exactly as two far-apart portals
     * that each happen to have that block on the corresponding corner. The only consequence is a
     * building one — editing the shared corner changes both addresses at once.
     */
    public List<BlockPos> corners() {
        Direction right = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        int w = rectangle.axis1Size;
        int h = rectangle.axis2Size;
        BlockPos min = rectangle.minCorner;

        BlockPos bottomLeft = min.relative(right, -1).below();
        BlockPos bottomRight = min.relative(right, w).below();
        BlockPos topLeft = min.relative(right, -1).above(h);
        BlockPos topRight = min.relative(right, w).above(h);

        return List.of(bottomLeft, bottomRight, topLeft, topRight);
    }

    /**
     * True if {@code pos} lies inside this rectangle's interior (on the portal's own plane, within
     * its width/height bounds). Used to dedup POI records — the POI index stores one record per
     * portal block, so a 2x3 portal yields 6 records that must all resolve to the same frame.
     */
    public boolean containsInterior(BlockPos pos) {
        Direction.Axis otherHorizontal = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        if (pos.get(otherHorizontal) != rectangle.minCorner.get(otherHorizontal)) {
            return false;
        }

        int alongAxis = pos.get(axis) - rectangle.minCorner.get(axis);
        int alongY = pos.getY() - rectangle.minCorner.getY();
        return alongAxis >= 0 && alongAxis < rectangle.axis1Size && alongY >= 0 && alongY < rectangle.axis2Size;
    }
}
