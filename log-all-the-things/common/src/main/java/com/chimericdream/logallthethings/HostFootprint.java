package com.chimericdream.logallthethings;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Geometry helpers shared by every "logged" block whose overlay has to fit onto half a straight
 * stair's footprint (the low-run/flat side, not the raised corner) — carpet-logging's
 * {@code CarpetedBlock#carpetShape} and snow-logging's {@code SnowedBlock#snowShape} both rotate the
 * same axis-aligned footprint per facing.
 */
public final class HostFootprint {
    private HostFootprint() {
    }

    /**
     * Same per-facing degree table used to generate {@code CarpetFrameRenderer}'s pre-rotated
     * {@code stairs_carpet_<facing>.json} / {@code top_stairs_carpet_<facing>.json} files from their
     * {@code _east} original — kept in sync deliberately so a stair's collision/overlay geometry
     * always agrees with what gets rendered for it.
     */
    public static int stairsYRotation(Direction facing) {
        return switch (facing) {
            case EAST -> 0;
            case SOUTH -> 270;
            case WEST -> 180;
            case NORTH -> 90;
            default -> 0;
        };
    }

    /**
     * Rotates an axis-aligned XZ footprint about the block's vertical center by a multiple of 90
     * degrees, using the same rotation direction as {@code PoseStack#mulPose(Axis.YP.rotationDegrees)}
     * (the mechanism {@code CarpetFrameRenderer} uses to orient the drawn mesh) so collision shapes
     * and rendered overlays never disagree about which side of the block is "flat."
     */
    public static VoxelShape rotateFootprintY(double x0, double y0, double z0, double x1, double y1, double z1, int degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (double[] corner : new double[][]{{x0, z0}, {x1, z0}, {x1, z1}, {x0, z1}}) {
            double px = corner[0] - 0.5;
            double pz = corner[1] - 0.5;
            double rx = px * cos + pz * sin + 0.5;
            double rz = -px * sin + pz * cos + 0.5;

            minX = Math.min(minX, rx);
            maxX = Math.max(maxX, rx);
            minZ = Math.min(minZ, rz);
            maxZ = Math.max(maxZ, rz);
        }

        return Shapes.box(minX, y0, minZ, maxX, y1, maxZ);
    }
}
