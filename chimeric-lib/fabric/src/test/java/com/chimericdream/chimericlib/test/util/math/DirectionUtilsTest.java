package com.chimericdream.chimericlib.test.util.math;

import com.chimericdream.lib.util.math.DirectionUtils;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionUtilsTest {
    @Test
    void getHitFaceTest() {
        // X-axis
        assertEquals(Direction.NORTH, DirectionUtils.getHitFace(Direction.Axis.X, Direction.DOWN));
        assertEquals(Direction.SOUTH, DirectionUtils.getHitFace(Direction.Axis.X, Direction.UP));
        assertEquals(Direction.WEST, DirectionUtils.getHitFace(Direction.Axis.X, Direction.NORTH));
        assertEquals(Direction.EAST, DirectionUtils.getHitFace(Direction.Axis.X, Direction.SOUTH));
        assertEquals(Direction.DOWN, DirectionUtils.getHitFace(Direction.Axis.X, Direction.WEST));
        assertEquals(Direction.UP, DirectionUtils.getHitFace(Direction.Axis.X, Direction.EAST));

        // Y-axis passes the hit side through unchanged
        for (Direction hitSide : Direction.values()) {
            assertEquals(hitSide, DirectionUtils.getHitFace(Direction.Axis.Y, hitSide));
        }

        // Z-axis
        assertEquals(Direction.NORTH, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.DOWN));
        assertEquals(Direction.SOUTH, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.UP));
        assertEquals(Direction.UP, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.NORTH));
        assertEquals(Direction.DOWN, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.SOUTH));
        assertEquals(Direction.WEST, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.WEST));
        assertEquals(Direction.EAST, DirectionUtils.getHitFace(Direction.Axis.Z, Direction.EAST));
    }
}
