package com.chimericdream.lib.util.math;

import net.minecraft.util.math.Direction;

public class DirectionUtils {
    public static Direction getHitFace(Direction.Axis axis, Direction hitSide) {
        return switch (axis) {
            case X -> switch (hitSide) {
                case DOWN -> Direction.NORTH;
                case UP -> Direction.SOUTH;
                case NORTH -> Direction.WEST;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.DOWN;
                case EAST -> Direction.UP;
            };
            case Direction.Axis.Y -> hitSide;
            case Z -> switch (hitSide) {
                case DOWN -> Direction.NORTH;
                case UP -> Direction.SOUTH;
                case NORTH -> Direction.UP;
                case SOUTH -> Direction.DOWN;
                case WEST -> Direction.WEST;
                case EAST -> Direction.EAST;
            };
        };
    }
}
