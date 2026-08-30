package com.chimericdream.logallthethings.windowlog.client;

import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;

import com.chimericdream.logallthethings.windowlog.client.WindowFramePaneTextures.PaneSprites;

/**
 * Renders the glass "frame" that fills a window-logged stair/slab's open notch, using hand-authored
 * per-shape geometry (see {@link WindowFrameGeometry}) instead of a generic flat pane. Only covers the
 * shape/half/facing combinations a model file actually exists for — see the {@code select} dispatch
 * table below for the current naming convention and which permutations are still missing. Falls back
 * to the caller's own flat-pane rendering (unchanged from before this) for anything not yet covered.
 */
public final class WindowFrameRenderer {
    private WindowFrameRenderer() {
    }

    /**
     * @return {@code true} if frame geometry was found and submitted; {@code false} if the caller
     * should fall back to its own (flat pane) rendering instead.
     */
    public static boolean submit(PoseStack poseStack, SubmitNodeCollector queue, int lightCoords, BlockState hostState, BlockState windowState) {
        Selection selection = select(hostState);
        if (selection == null) {
            return false;
        }

        Optional<WindowFrameGeometry> geometry = WindowFrameGeometryCache.get(selection.modelName);
        if (geometry.isEmpty()) {
            return false;
        }

        Optional<PaneSprites> sprites = WindowFramePaneTextures.get(windowState.getBlock());
        if (sprites.isEmpty()) {
            return false;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(selection.yRotation));
        poseStack.translate(-0.5, -0.5, -0.5);

        WindowFrameGeometry resolvedGeometry = geometry.get();
        PaneSprites resolvedSprites = sprites.get();
        queue.submitCustomGeometry(
            poseStack,
            RenderTypes.translucentMovingBlock(),
            (pose, buffer) -> renderGeometry(pose, buffer, resolvedGeometry, resolvedSprites, lightCoords)
        );

        poseStack.popPose();

        return true;
    }

    private static void renderGeometry(PoseStack.Pose pose, VertexConsumer buffer, WindowFrameGeometry geometry, PaneSprites sprites, int light) {
        for (WindowFrameGeometry.Element element : geometry.elements()) {
            float x0 = element.from()[0] / 16f;
            float y0 = element.from()[1] / 16f;
            float z0 = element.from()[2] / 16f;
            float x1 = element.to()[0] / 16f;
            float y1 = element.to()[1] / 16f;
            float z1 = element.to()[2] / 16f;

            for (Map.Entry<Direction, WindowFrameGeometry.Face> entry : element.faces().entrySet()) {
                Direction direction = entry.getKey();
                WindowFrameGeometry.Face face = entry.getValue();
                TextureAtlasSprite sprite = face.paneTextureSlot() == 1 ? sprites.flat() : sprites.edge();

                emitFace(pose, buffer, direction, x0, y0, z0, x1, y1, z1, face.uv(), face.rotation(), sprite, light, element.rotation());
            }
        }
    }

    private static void emitFace(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        Direction direction,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float[] uv,
        int rotation,
        TextureAtlasSprite sprite,
        int light,
        WindowFrameGeometry.Rotation elementRotation
    ) {
        float[][] corners = corners(direction, x0, y0, z0, x1, y1, z1);
        // TextureAtlasSprite#getU/getV take a 0-1 fraction of the sprite, not the model JSON's 0-16
        // pixel convention.
        float u0 = uv[0] / 16f;
        float v0 = uv[1] / 16f;
        float u1 = uv[2] / 16f;
        float v1 = uv[3] / 16f;
        float[][] uvCorners = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
        int shift = ((rotation / 90) % 4 + 4) % 4;

        float[] normal = rotateVector(new float[]{direction.getStepX(), direction.getStepY(), direction.getStepZ()}, elementRotation);
        float[] backNormal = {-normal[0], -normal[1], -normal[2]};

        // translucentMovingBlock() culls backfaces. A real pane is visible from either side, so emit
        // the quad in both winding orders instead of chasing which order a given element's rotation
        // ends up front-facing from - reversed order + negated normal is the mirror image of the loop
        // below, needed once per quad regardless of rotation.
        for (int i = 0; i < 4; i++) {
            float[] pos = rotatePoint(corners[i], elementRotation);
            float[] tex = uvCorners[(i + shift) % 4];

            buffer.addVertex(pose.pose(), pos[0], pos[1], pos[2])
                .setColor(1f, 1f, 1f, 1f)
                .setUv(sprite.getU(tex[0]), sprite.getV(tex[1]))
                .setLight(light)
                .setNormal(normal[0], normal[1], normal[2]);
        }
        for (int i = 3; i >= 0; i--) {
            float[] pos = rotatePoint(corners[i], elementRotation);
            float[] tex = uvCorners[(i + shift) % 4];

            buffer.addVertex(pose.pose(), pos[0], pos[1], pos[2])
                .setColor(1f, 1f, 1f, 1f)
                .setUv(sprite.getU(tex[0]), sprite.getV(tex[1]))
                .setLight(light)
                .setNormal(backNormal[0], backNormal[1], backNormal[2]);
        }
    }

    /**
     * Applies a hand-authored element's {@link WindowFrameGeometry.Rotation} (Blockbench pivot
     * rotation, in the model's 0-16 pixel space) to a point already scaled into 0-1 block space.
     * {@code null} (no rotation authored for this element) is the identity transform.
     */
    private static float[] rotatePoint(float[] point, WindowFrameGeometry.Rotation rotation) {
        if (rotation == null) {
            return point;
        }

        float ox = rotation.origin()[0] / 16f;
        float oy = rotation.origin()[1] / 16f;
        float oz = rotation.origin()[2] / 16f;

        float[] rotated = rotateVector(new float[]{point[0] - ox, point[1] - oy, point[2] - oz}, rotation);

        return new float[]{rotated[0] + ox, rotated[1] + oy, rotated[2] + oz};
    }

    /** Rotates a direction vector (no translation) by X, then Y, then Z — Blockbench's own composition order. */
    private static float[] rotateVector(float[] vector, WindowFrameGeometry.Rotation rotation) {
        if (rotation == null) {
            return vector;
        }

        float[] afterX = rotateAxis(vector[0], vector[1], vector[2], rotation.x(), Axis3.X);
        float[] afterY = rotateAxis(afterX[0], afterX[1], afterX[2], rotation.y(), Axis3.Y);
        return rotateAxis(afterY[0], afterY[1], afterY[2], rotation.z(), Axis3.Z);
    }

    private enum Axis3 {X, Y, Z}

    private static float[] rotateAxis(float x, float y, float z, float degrees, Axis3 axis) {
        if (degrees == 0) {
            return new float[]{x, y, z};
        }

        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        return switch (axis) {
            case X -> new float[]{x, y * cos - z * sin, y * sin + z * cos};
            case Y -> new float[]{x * cos + z * sin, y, -x * sin + z * cos};
            case Z -> new float[]{x * cos - y * sin, x * sin + y * cos, z};
        };
    }

    private static float[][] corners(Direction direction, float x0, float y0, float z0, float x1, float y1, float z1) {
        return switch (direction) {
            case DOWN -> new float[][]{{x0, y0, z0}, {x0, y0, z1}, {x1, y0, z1}, {x1, y0, z0}};
            case UP -> new float[][]{{x0, y1, z1}, {x0, y1, z0}, {x1, y1, z0}, {x1, y1, z1}};
            case NORTH -> new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
            case SOUTH -> new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
            case WEST -> new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
            case EAST -> new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
        };
    }

    /**
     * Only {@code StairsShape.STRAIGHT} stairs can be window-logged at all ({@code WindowLogHelper}
     * refuses the interaction for inner/outer corners), so there is exactly one stairs geometry file
     * per half — {@code stairs_ew_pane.json} / {@code top_stairs_ew_pane.json} — reused for all four
     * facings by rotating it around Y. The {@code _top} variant bakes its own vertical flip into each
     * element's Blockbench pivot rotation (see {@link WindowFrameGeometry.Rotation}), so no extra
     * matrix rotation is needed here for it either.
     */
    private static Selection select(BlockState hostState) {
        if (hostState.getBlock() instanceof StairBlock) {
            if (hostState.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                return null;
            }

            Direction facing = hostState.getValue(StairBlock.FACING);
            boolean top = hostState.getValue(StairBlock.HALF) == Half.TOP;

            int baseY = switch (facing) {
                case EAST -> 0;
                case SOUTH -> 90;
                case WEST -> 180;
                case NORTH -> 270;
                default -> 0;
            };

            return new Selection(top ? "top_stairs_ew_pane" : "stairs_ew_pane", baseY);
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            // Slabs have no facing to derive an axis from (symmetric either way) - "ew" is an
            // arbitrary but consistent default, matching WindowLogHelper#orientWindowPane's fallback.
            return new Selection((top ? "slab_top_" : "slab_") + "ew_pane", 0);
        }

        return null;
    }

    private record Selection(String modelName, int yRotation) {
    }
}
