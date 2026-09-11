package com.chimericdream.allhallowssteve.client.render;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Rotation;

/**
 * Draws a decorated pumpkin's carved stencil overlays: one flat, translucent decal per horizontal face
 * that has a stencil, flush against the pumpkin's own real face. Shared between
 * {@link DecoratedPumpkinBlockEntityRenderer} (the placed block, with a real {@code FACING} and level
 * lighting) and {@link DecoratedPumpkinItemRenderer} (the item form, which has neither) so both render
 * identically.
 */
public final class DecoratedPumpkinStencilRenderer {
    private static final float[] FULL_FACE_UV = {0f, 0f, 16f, 16f};
    private static final Direction[] CANONICAL_FACES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private DecoratedPumpkinStencilRenderer() {
    }

    /**
     * @param facing the block's current {@code FACING} (block form), or {@link Direction#NORTH} — the
     *               component's own default orientation — for the item form, which has no placement.
     */
    public static void submit(
        PoseStack poseStack,
        SubmitNodeCollector queue,
        Direction facing,
        PumpkinStencilsComponent stencils,
        CardinalLighting cardinalLighting,
        int light
    ) {
        if (stencils.isEmpty()) {
            return;
        }

        Rotation rotation = rotationFromNorth(facing);

        queue.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, buffer) -> {
            for (Direction canonicalFace : CANONICAL_FACES) {
                stencils.get(canonicalFace).ifPresent(stencil -> {
                    Direction worldFace = rotation.rotate(canonicalFace);
                    float shade = cardinalLighting.byFace(worldFace);

                    QuadEmitter.emitFace(pose, buffer, worldFace, 0f, 0f, 0f, 1f, 1f, 1f, FULL_FACE_UV, 0, sprite(stencil), light, shade);
                });
            }
        });
    }

    /**
     * The rotation the block's own model was baked with for a given {@code FACING}, matching
     * {@code DecoratedPumpkinBlockDataGenerator}'s horizontal-facing dispatch (north = no rotation,
     * east = 90°, south = 180°, west = 270°) — a stencil stored against the component's own default
     * ("north") face needs to land on the same real-world face the rotated block model does.
     */
    private static Rotation rotationFromNorth(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static TextureAtlasSprite sprite(String stencil) {
        Identifier texture = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/decorated_pumpkin/overlays/" + stencil);
        return Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, texture));
    }
}
