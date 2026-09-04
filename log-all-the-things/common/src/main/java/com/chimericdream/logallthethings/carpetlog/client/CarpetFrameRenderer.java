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

import com.chimericdream.logallthethings.client.QuadEmitter;

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
                QuadEmitter.emitFace(pose, buffer, direction, x0, y0, z0, x1, y1, z1, face.uv(), face.rotation(), sprite, light, shade);
            }
        }
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
