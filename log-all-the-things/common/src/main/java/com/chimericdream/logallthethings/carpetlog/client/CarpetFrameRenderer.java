package com.chimericdream.logallthethings.carpetlog.client;

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

/**
 * Renders the carpet "overlay" that lies across a carpet-logged stair/slab, using hand-authored
 * per-shape geometry (see {@link CarpetFrameGeometry}) instead of a generic flat carpet. Only covers
 * the shape/half/facing combinations a model file actually exists for — see the {@code select}
 * dispatch table below. Falls back to the caller's own flat-carpet rendering for anything not covered
 * (currently just non-straight stairs, which {@code CarpetLogHelper} refuses to log in the first
 * place). Mirrors {@code windowlog.client.WindowFrameRenderer}, minus the flat/edge texture split and
 * the per-element Blockbench pivot rotation support - none of this set's carpet elements use one.
 */
public final class CarpetFrameRenderer {
    private CarpetFrameRenderer() {
    }

    /**
     * @return {@code true} if overlay geometry was found and submitted; {@code false} if the caller
     * should fall back to its own (flat carpet) rendering instead.
     */
    public static boolean submit(PoseStack poseStack, SubmitNodeCollector queue, int lightCoords, BlockState hostState, BlockState carpetState) {
        Selection selection = select(hostState);
        if (selection == null) {
            return false;
        }

        Optional<CarpetFrameGeometry> geometry = CarpetFrameGeometryCache.get(selection.modelName);
        if (geometry.isEmpty()) {
            return false;
        }

        Optional<TextureAtlasSprite> sprite = CarpetFrameTextures.get(carpetState.getBlock());
        if (sprite.isEmpty()) {
            return false;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(selection.yRotation));
        poseStack.translate(-0.5, -0.5, -0.5);

        CarpetFrameGeometry resolvedGeometry = geometry.get();
        TextureAtlasSprite resolvedSprite = sprite.get();
        queue.submitCustomGeometry(
            poseStack,
            RenderTypes.solidMovingBlock(),
            (pose, buffer) -> renderGeometry(pose, buffer, resolvedGeometry, resolvedSprite, lightCoords)
        );

        poseStack.popPose();

        return true;
    }

    private static void renderGeometry(PoseStack.Pose pose, VertexConsumer buffer, CarpetFrameGeometry geometry, TextureAtlasSprite sprite, int light) {
        for (CarpetFrameGeometry.Element element : geometry.elements()) {
            float x0 = element.from()[0] / 16f;
            float y0 = element.from()[1] / 16f;
            float z0 = element.from()[2] / 16f;
            float x1 = element.to()[0] / 16f;
            float y1 = element.to()[1] / 16f;
            float z1 = element.to()[2] / 16f;

            for (Map.Entry<Direction, CarpetFrameGeometry.Face> entry : element.faces().entrySet()) {
                CarpetFrameGeometry.Face face = entry.getValue();
                emitFace(pose, buffer, entry.getKey(), x0, y0, z0, x1, y1, z1, face.uv(), face.rotation(), sprite, light);
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
        int light
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
        // corners() for UP/DOWN is reordered relative to NORTH/SOUTH/EAST/WEST to fix backface culling
        // (see WindowFrameRenderer, whose corners() this mirrors exactly), which reverses uv traversal
        // for those two directions relative to how uvCorners was authored.
        boolean reverseUv = direction == Direction.UP || direction == Direction.DOWN;

        float[] normal = {direction.getStepX(), direction.getStepY(), direction.getStepZ()};

        for (int i = 0; i < 4; i++) {
            float[] pos = corners[i];
            int uvIndex = reverseUv ? (4 - i) % 4 : i;
            float[] tex = uvCorners[(uvIndex + shift) % 4];

            buffer.addVertex(pose.pose(), pos[0], pos[1], pos[2])
                .setColor(1f, 1f, 1f, 1f)
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
     * per half — {@code stairs_carpet.json} / {@code top_stairs_carpet.json} — reused for all four
     * facings by rotating it around Y. Both files are authored assuming {@code FACING == EAST}; the
     * per-facing degree table here matches {@code CarpetedBlock#stairsYRotation} exactly so the
     * collision box drawn always agrees with what's rendered.
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
                case SOUTH -> 270;
                case WEST -> 180;
                case NORTH -> 90;
                default -> 0;
            };

            return new Selection(top ? "top_stairs_carpet" : "stairs_carpet", baseY);
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            return new Selection(top ? "slab_top_carpet" : "slab_carpet", 0);
        }

        return null;
    }

    private record Selection(String modelName, int yRotation) {
    }
}
