package com.chimericdream.betterportallinking.test.portal;

import com.chimericdream.betterportallinking.portal.PortalFrame;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PortalFrame} is level-independent: {@link PortalFrame#resolve} is driven here with a
 * lambda over a plain {@link Set} of interior positions instead of a real {@code Level}.
 *
 * <p>Corner expectations are derived by hand from the documented formula (see
 * {@link PortalFrame#corners()}): with {@code right = Direction.get(POSITIVE, axis)},
 * {@code min = rectangle.minCorner}, {@code w = axis1Size}, {@code h = axis2Size}:
 * <pre>
 * bottomLeft  = min.relative(right, -1).below()
 * bottomRight = min.relative(right,  w).below()
 * topLeft     = min.relative(right, -1).above(h)
 * topRight    = min.relative(right,  w).above(h)
 * </pre>
 */
public class PortalFrameTest extends BootstrapMinecraft {
    /**
     * Builds the interior block-position set for a {@code w x h} portal whose interior
     * bottom-left block is {@code base}, growing along {@code right} (the positive direction of
     * {@code axis}) and upward in Y. {@code base} is therefore exactly the {@code minCorner} the
     * vanilla rectangle search is expected to resolve.
     */
    private static Set<BlockPos> interiorSet(BlockPos base, Direction.Axis axis, int w, int h) {
        Direction right = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Set<BlockPos> positions = new HashSet<>();
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                positions.add(base.relative(right, dx).above(dy));
            }
        }
        return positions;
    }

    @Test
    void cornersFor2x3PortalOnAxisX() {
        BlockPos base = new BlockPos(10, 20, 30);
        int w = 2;
        int h = 3;
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.X, w, h);

        PortalFrame frame = PortalFrame.resolve(interior::contains, base, Direction.Axis.X);

        assertEquals(base, frame.rectangle().minCorner);
        assertEquals(w, frame.rectangle().axis1Size);
        assertEquals(h, frame.rectangle().axis2Size);
        assertEquals(Direction.Axis.X, frame.axis());

        List<BlockPos> corners = frame.corners();
        assertEquals(4, corners.size());
        assertTrue(corners.contains(new BlockPos(9, 19, 30)), "bottomLeft");
        assertTrue(corners.contains(new BlockPos(12, 19, 30)), "bottomRight");
        assertTrue(corners.contains(new BlockPos(9, 23, 30)), "topLeft");
        assertTrue(corners.contains(new BlockPos(12, 23, 30)), "topRight");
    }

    @Test
    void cornersFor2x3PortalOnAxisZ() {
        BlockPos base = new BlockPos(10, 20, 30);
        int w = 2;
        int h = 3;
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.Z, w, h);

        PortalFrame frame = PortalFrame.resolve(interior::contains, base, Direction.Axis.Z);

        assertEquals(base, frame.rectangle().minCorner);
        assertEquals(w, frame.rectangle().axis1Size);
        assertEquals(h, frame.rectangle().axis2Size);
        assertEquals(Direction.Axis.Z, frame.axis());

        List<BlockPos> corners = frame.corners();
        assertEquals(4, corners.size());
        assertTrue(corners.contains(new BlockPos(10, 19, 29)), "bottomLeft");
        assertTrue(corners.contains(new BlockPos(10, 19, 32)), "bottomRight");
        assertTrue(corners.contains(new BlockPos(10, 23, 29)), "topLeft");
        assertTrue(corners.contains(new BlockPos(10, 23, 32)), "topRight");
    }

    @Test
    void cornersScaleWithNonStandardSize() {
        // 3 wide x 4 tall, proving the corner math isn't hardcoded to the common 2x3 shape.
        BlockPos base = new BlockPos(0, 0, 0);
        int w = 3;
        int h = 4;
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.X, w, h);

        PortalFrame frame = PortalFrame.resolve(interior::contains, base, Direction.Axis.X);

        assertEquals(base, frame.rectangle().minCorner);
        assertEquals(w, frame.rectangle().axis1Size);
        assertEquals(h, frame.rectangle().axis2Size);

        List<BlockPos> corners = frame.corners();
        assertTrue(corners.contains(new BlockPos(-1, -1, 0)), "bottomLeft");
        assertTrue(corners.contains(new BlockPos(3, -1, 0)), "bottomRight");
        assertTrue(corners.contains(new BlockPos(-1, 4, 0)), "topLeft");
        assertTrue(corners.contains(new BlockPos(3, 4, 0)), "topRight");
    }

    @Test
    void resolveFindsSameRectangleFromAnyInteriorStart() {
        // Production code passes whatever portal-block position it happens to have (a POI record),
        // not necessarily the min corner, so resolve() must find the same rectangle regardless.
        BlockPos base = new BlockPos(100, 60, -50);
        int w = 2;
        int h = 3;
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.X, w, h);
        Direction right = Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X);
        BlockPos middleStart = base.relative(right, 1).above(1);

        PortalFrame frame = PortalFrame.resolve(interior::contains, middleStart, Direction.Axis.X);

        assertEquals(base, frame.rectangle().minCorner);
        assertEquals(w, frame.rectangle().axis1Size);
        assertEquals(h, frame.rectangle().axis2Size);
    }

    @Test
    void containsInteriorTrueForEveryInteriorBlockFalseForCornersAndOutside() {
        BlockPos base = new BlockPos(0, 0, 0);
        int w = 3;
        int h = 4;
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.X, w, h);

        PortalFrame frame = PortalFrame.resolve(interior::contains, base, Direction.Axis.X);

        for (BlockPos pos : interior) {
            assertTrue(frame.containsInterior(pos), "expected interior block " + pos + " to be inside the frame");
        }

        for (BlockPos corner : frame.corners()) {
            assertFalse(frame.containsInterior(corner), "corner " + corner + " must not be interior");
        }

        // Co-planar (same Z) but outside the w x h bounds on both axes.
        assertFalse(frame.containsInterior(new BlockPos(3, 0, 0)), "one past the right edge");
        assertFalse(frame.containsInterior(new BlockPos(0, 4, 0)), "one past the top edge");
        assertFalse(frame.containsInterior(new BlockPos(-1, 0, 0)), "one past the left edge");
        assertFalse(frame.containsInterior(new BlockPos(0, -1, 0)), "one past the bottom edge");
    }

    @Test
    void containsInteriorRejectsPositionsOnAParallelPlane() {
        // Two portals one block apart on parallel planes share the same width/height bounds. Only
        // the cross-plane check distinguishes them; without it the neighbour's blocks would fall
        // inside this frame and the two portals would be scored as a single candidate.
        BlockPos base = new BlockPos(0, 0, 0);
        Set<BlockPos> interior = interiorSet(base, Direction.Axis.X, 2, 3);

        PortalFrame frame = PortalFrame.resolve(interior::contains, base, Direction.Axis.X);

        for (BlockPos pos : interior) {
            assertTrue(frame.containsInterior(pos), pos + " is on the frame's own plane");
            assertFalse(frame.containsInterior(pos.south()), pos.south() + " is on a parallel plane");
            assertFalse(frame.containsInterior(pos.north()), pos.north() + " is on a parallel plane");
        }
    }
}
