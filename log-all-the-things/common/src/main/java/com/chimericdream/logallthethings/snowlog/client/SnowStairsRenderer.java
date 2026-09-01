package com.chimericdream.logallthethings.snowlog.client;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.chimericdream.logallthethings.client.QuadEmitter;
import com.chimericdream.logallthethings.snowlog.SnowedBlock;

/**
 * Renders the snow overlay on a snow-logged straight stairs' low-run/flat-side half footprint - the
 * one host shape whose open space isn't a full-footprint column, so vanilla's real
 * {@code SnowLayerBlock} model (always full-footprint) can't be submitted directly the way
 * {@code SnowedBlockEntityRenderer} does for every other host. Unlike carpet-logging's
 * {@code CarpetFrameRenderer}, this needs no hand-authored per-facing/per-height model files: the
 * overlay is always a single axis-aligned box, so its geometry is read straight off
 * {@link SnowedBlock#snowShape} (the exact shape already used for collision) via
 * {@code VoxelShape#bounds()} - collision and render can never disagree about where the box sits.
 */
public final class SnowStairsRenderer {
    private SnowStairsRenderer() {
    }

    public static void submit(PoseStack poseStack, SubmitNodeCollector queue, int[] faceLight, CardinalLighting cardinalLighting, BlockState hostState, BlockState snowState) {
        int layers = SnowedBlock.layersOf(snowState);
        if (layers <= 0) {
            return;
        }

        Optional<TextureAtlasSprite> sprite = SnowTextures.get(snowState.getBlock());
        if (sprite.isEmpty()) {
            return;
        }

        AABB box = SnowedBlock.snowShape(hostState, layers).bounds();
        TextureAtlasSprite resolvedSprite = sprite.get();

        queue.submitCustomGeometry(
            poseStack,
            RenderTypes.solidMovingBlock(),
            (pose, buffer) -> renderBox(pose, buffer, box, resolvedSprite, faceLight, cardinalLighting)
        );
    }

    /**
     * Matches vanilla's own {@code snow_height*.json} models: the top face tiles across the real
     * footprint size (not stretched to a fixed 16px), and side faces always sample the texture's own
     * bottom-anchored band, so a shorter/taller pile shows a shorter/taller slice of the same texture
     * instead of stretching it. The bottom face is never emitted - it's always flush against solid
     * host/floor geometry, so it's never visible.
     */
    private static void renderBox(PoseStack.Pose pose, VertexConsumer buffer, AABB box, TextureAtlasSprite sprite, int[] faceLight, CardinalLighting cardinalLighting) {
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;

        float xSizePx = (x1 - x0) * 16f;
        float ySizePx = (y1 - y0) * 16f;
        float zSizePx = (z1 - z0) * 16f;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }

            float[] uv = switch (direction) {
                case UP -> new float[]{0f, 0f, xSizePx, zSizePx};
                case NORTH, SOUTH -> new float[]{0f, 16f - ySizePx, xSizePx, 16f};
                case EAST, WEST -> new float[]{0f, 16f - ySizePx, zSizePx, 16f};
                default -> new float[]{0f, 0f, 16f, 16f};
            };

            int light = faceLight[direction.get3DDataValue()];
            float shade = cardinalLighting.byFace(direction);
            QuadEmitter.emitFace(pose, buffer, direction, x0, y0, z0, x1, y1, z1, uv, 0, sprite, light, shade);
        }
    }
}
