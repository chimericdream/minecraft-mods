package com.chimericdream.logallthethings.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

/**
 * Emits a single textured quad for one face of a hand-cut, axis-aligned box — the low-level vertex
 * math shared by every overlay renderer in this mod that draws geometry vanilla's block model system
 * doesn't have a model for (carpet-logging's {@code CarpetFrameRenderer}, snow-logging's stairs
 * overlay). Callers own the element/UV data (whether hand-authored in a Blockbench JSON file or
 * computed procedurally); this only knows how to turn one face of one box into vertices.
 */
public final class QuadEmitter {
    /**
     * Two coplanar quads at the same depth z-fight (flicker between which one wins per pixel) — this
     * mod's overlays are always drawn flush against real host geometry (a slab's own top face, a
     * stair's own low-run/corner-top surface, a snow overlay sitting exactly at the open space's
     * boundary). Nudging every vertex outward along its own face normal by a sliver moves the overlay
     * just in front of the coincident host face, resolving the tie without any visible gap.
     */
    public static final float SURFACE_NUDGE = 1f / 2048f;

    private QuadEmitter() {
    }

    public static void emitFace(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        Direction direction,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float[] uv,
        int rotation,
        TextureAtlasSprite sprite,
        int light,
        float shade
    ) {
        float[][] corners = corners(direction, x0, y0, z0, x1, y1, z1);
        // TextureAtlasSprite#getU/getV take a 0-1 fraction of the sprite, not the model JSON's 0-16
        // pixel convention.
        float u0 = uv[0] / 16f;
        float v0 = uv[1] / 16f;
        float u1 = uv[2] / 16f;
        float v1 = uv[3] / 16f;
        int rotationSteps = ((rotation / 90) % 4 + 4) % 4;
        // corners() for UP/DOWN is reordered relative to NORTH/SOUTH/EAST/WEST to fix backface culling,
        // which reverses uv traversal for those two directions relative to how uvCorners was authored.
        boolean reverseUv = direction == Direction.UP || direction == Direction.DOWN;

        float[] normal = {direction.getStepX(), direction.getStepY(), direction.getStepZ()};

        for (int i = 0; i < 4; i++) {
            float[] pos = corners[i];
            int uvIndex = reverseUv ? (4 - i) % 4 : i;
            // Canonical unit-square corner matching uvIndex's own traversal order: (0,0), (0,1),
            // (1,1), (1,0). Expressing the corner parametrically (rather than picking directly from a
            // 4-entry UV corner list) means the model's own "rotation" field composes with linear
            // interpolation below in a way that naturally honors any flip already baked into the raw
            // uv values (u0 > u1 or v0 > v1) without needing to special-case it.
            float s = (uvIndex == 0 || uvIndex == 1) ? 0f : 1f;
            float t = (uvIndex == 1 || uvIndex == 2) ? 1f : 0f;
            for (int step = 0; step < rotationSteps; step++) {
                float ns = t;
                float nt = 1f - s;
                s = ns;
                t = nt;
            }
            float[] tex = {u0 + (u1 - u0) * s, v0 + (v1 - v0) * t};

            buffer.addVertex(
                    pose.pose(),
                    pos[0] + normal[0] * SURFACE_NUDGE,
                    pos[1] + normal[1] * SURFACE_NUDGE,
                    pos[2] + normal[2] * SURFACE_NUDGE
                )
                .setColor(shade, shade, shade, 1f)
                .setUv(sprite.getU(tex[0]), sprite.getV(tex[1]))
                .setLight(light)
                .setNormal(pose, normal[0], normal[1], normal[2]);
        }
    }

    public static float[][] corners(Direction direction, float x0, float y0, float z0, float x1, float y1, float z1) {
        return switch (direction) {
            case DOWN -> new float[][]{{x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}, {x0, y0, z1}};
            case UP -> new float[][]{{x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}, {x0, y1, z0}};
            case NORTH -> new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
            case SOUTH -> new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
            case WEST -> new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
            case EAST -> new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
        };
    }
}
