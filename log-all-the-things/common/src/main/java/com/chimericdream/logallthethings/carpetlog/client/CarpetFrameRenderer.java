package com.chimericdream.logallthethings.carpetlog.client;

import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;

/**
 * Renders the carpet "overlay" that lies across a carpet-logged stair/slab, using hand-authored
 * per-shape geometry (see {@link CarpetFrameGeometry}) instead of a generic flat carpet. Only covers
 * the shape/half/facing combinations a model file actually exists for — see the {@code select}
 * dispatch table below. Falls back to the caller's own flat-carpet rendering for anything not covered:
 * non-straight stairs (which {@code CarpetLogHelper} refuses to log in the first place), and every
 * carpetable host that isn't a stair or slab at all (walls, fences, chains, bars, glass panes) - those
 * have no flat surface of their own to fit an overlay to, so the fallback's plain floor-level carpet
 * (matching a real {@code CarpetBlock}'s own shape) is already the correct look with no overlay
 * authoring needed. Mirrors {@code windowlog.client.WindowFrameRenderer}, minus the flat/edge texture
 * split and the per-element Blockbench pivot rotation support - none of this set's carpet elements use
 * one.
 *
 * <p>Unlike the window-pane set (one file per shape, reused for all four facings by rotating the
 * whole mesh at render time), stairs carpet has one file <em>per facing</em>
 * ({@code stairs_carpet_east.json}, {@code _south}, {@code _west}, {@code _north}, and the
 * {@code top_stairs_carpet_*} equivalents). A shared-and-rotated mesh was tried first and reverted:
 * the low-run/corner-top elements are each only half the block's footprint (non-square), and rotating
 * a non-square footprint 90 degrees swaps which world axis is wide versus narrow - no UV
 * transform on a single shared crop can compensate for that without stretching, since the actual
 * texel density along each world axis would need to differ between facings. Baking each facing's
 * geometry into its own file (each with UV crops proportioned for its own real, un-rotated footprint)
 * avoids the problem entirely. Slabs don't need this: a slab's carpet is a single full-footprint
 * (square) element with no facing to begin with, so one file already renders correctly as-is.
 */
public final class CarpetFrameRenderer {
    /**
     * Unlike a window pane (which fills a notch the host doesn't otherwise occupy), these carpet
     * models sit flush against real host geometry - e.g. a bottom slab's carpet top face and a real
     * (unmodified) {@code SlabBlock}'s own top face both land at exactly y=0.5, since the host renders
     * its own true, un-notched shape via {@code submitMovingBlock} regardless of what this overlay
     * draws. Two coplanar quads at the same depth z-fight (flicker between which one wins per pixel).
     * Nudging every vertex outward along its own face normal by a sliver moves this overlay's faces
     * just in front of the host's coincident ones, resolving the tie without any visible gap.
     * Package-visible: {@code CarpetedBlockEntityRenderer} reuses the same value to nudge its plain
     * flat-carpet fallback render (a coarser, whole-model version of the same fix - see its own use).
     */
    static final float SURFACE_NUDGE = 1f / 2048f;

    private CarpetFrameRenderer() {
    }

    /**
     * @param faceLight per-neighbor-direction packed light, indexed by {@link Direction#get3DDataValue()}
     *                  (see {@link com.chimericdream.logallthethings.client.FaceLighting}).
     * @return {@code true} if overlay geometry was found and submitted; {@code false} if the caller
     * should fall back to its own (flat carpet) rendering instead.
     */
    public static boolean submit(PoseStack poseStack, SubmitNodeCollector queue, int[] faceLight, CardinalLighting cardinalLighting, BlockState hostState, BlockState carpetState) {
        String modelName = select(hostState);
        if (modelName == null) {
            return false;
        }

        Optional<CarpetFrameGeometry> geometry = CarpetFrameGeometryCache.get(modelName);
        if (geometry.isEmpty()) {
            return false;
        }

        Optional<TextureAtlasSprite> sprite = CarpetFrameTextures.get(carpetState.getBlock());
        if (sprite.isEmpty()) {
            return false;
        }

        CarpetFrameGeometry resolvedGeometry = geometry.get();
        TextureAtlasSprite resolvedSprite = sprite.get();
        queue.submitCustomGeometry(
            poseStack,
            RenderTypes.solidMovingBlock(),
            (pose, buffer) -> renderGeometry(pose, buffer, resolvedGeometry, resolvedSprite, faceLight, cardinalLighting)
        );

        return true;
    }

    private static void renderGeometry(PoseStack.Pose pose, VertexConsumer buffer, CarpetFrameGeometry geometry, TextureAtlasSprite sprite, int[] faceLight, CardinalLighting cardinalLighting) {
        for (CarpetFrameGeometry.Element element : geometry.elements()) {
            float x0 = element.from()[0] / 16f;
            float y0 = element.from()[1] / 16f;
            float z0 = element.from()[2] / 16f;
            float x1 = element.to()[0] / 16f;
            float y1 = element.to()[1] / 16f;
            float z1 = element.to()[2] / 16f;

            for (Map.Entry<Direction, CarpetFrameGeometry.Face> entry : element.faces().entrySet()) {
                Direction direction = entry.getKey();
                CarpetFrameGeometry.Face face = entry.getValue();
                int light = faceLight[direction.get3DDataValue()];
                float shade = cardinalLighting.byFace(direction);
                emitFace(pose, buffer, direction, x0, y0, z0, x1, y1, z1, face.uv(), face.rotation(), sprite, light, shade);
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
        // corners() for UP/DOWN is reordered relative to NORTH/SOUTH/EAST/WEST to fix backface culling
        // (see WindowFrameRenderer, whose corners() this mirrors exactly), which reverses uv traversal
        // for those two directions relative to how uvCorners was authored.
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

    private static float[][] corners(Direction direction, float x0, float y0, float z0, float x1, float y1, float z1) {
        return switch (direction) {
            case DOWN -> new float[][]{{x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}, {x0, y0, z1}};
            case UP -> new float[][]{{x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}, {x0, y1, z0}};
            case NORTH -> new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
            case SOUTH -> new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
            case WEST -> new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
            case EAST -> new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
        };
    }

    /**
     * Only {@code StairsShape.STRAIGHT} stairs can be carpet-logged at all ({@code CarpetLogHelper}
     * refuses the interaction for inner/outer corners), so there is exactly one stairs geometry file
     * per half per facing — {@code stairs_carpet_<facing>.json} / {@code top_stairs_carpet_<facing>.json}.
     */
    private static String select(BlockState hostState) {
        if (hostState.getBlock() instanceof StairBlock) {
            if (hostState.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                return null;
            }

            Direction facing = hostState.getValue(StairBlock.FACING);
            boolean top = hostState.getValue(StairBlock.HALF) == Half.TOP;

            String facingName = switch (facing) {
                case EAST -> "east";
                case SOUTH -> "south";
                case WEST -> "west";
                case NORTH -> "north";
                default -> "east";
            };

            return (top ? "top_stairs_carpet_" : "stairs_carpet_") + facingName;
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            return top ? "slab_top_carpet" : "slab_carpet";
        }

        return null;
    }
}
