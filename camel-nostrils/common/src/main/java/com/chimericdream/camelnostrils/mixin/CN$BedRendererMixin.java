package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.block.UpsideDownBedBlock;
import com.chimericdream.camelnostrils.block.UpsideDownBedRenderStateDuck;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes vanilla's single, shared {@code BedRenderer} (registered once for
 * {@code BlockEntityType.BED} - see {@code ModBlockEntityValidBlocks} for why the upside-down beds
 * reuse that type instead of their own) draw upside-down beds hanging from the ceiling instead of
 * resting on the floor.
 * <p>
 * {@code extractRenderState} stamps a flag (via {@link UpsideDownBedRenderStateDuck}) recording whether the
 * block entity being rendered belongs to an {@link UpsideDownBedBlock}. {@code submit} redirects the
 * per-direction placement matrix it looks up, adding a 180-degree rotation about the horizontal axis
 * that runs lengthwise through the bed (head-to-foot) when that flag is set - a proper rotation, not
 * a mirror, so it doesn't invert face winding/culling the way a negative-scale flip would.
 */
@Mixin(BedRenderer.class)
public class CN$BedRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void cn$captureUpsideDown(
        BedBlockEntity blockEntity,
        BedRenderState state,
        float partialTicks,
        Vec3 cameraPosition,
        CrumblingOverlay breakProgress,
        CallbackInfo ci
    ) {
        ((UpsideDownBedRenderStateDuck) state).camelnostrils$setUpsideDown(blockEntity.getBlockState().getBlock() instanceof UpsideDownBedBlock);
    }

    @Redirect(
        method = "submit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BedRenderer;modelTransform(Lnet/minecraft/core/Direction;)Lcom/mojang/math/Transformation;"
        )
    )
    private Transformation cn$maybeFlipUpsideDown(
        Direction direction,
        BedRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState camera
    ) {
        Transformation upright = BedRenderer.modelTransform(direction);

        if (!((UpsideDownBedRenderStateDuck) state).camelnostrils$isUpsideDown()) {
            return upright;
        }

        Vector3f lengthwiseAxis = new Vector3f(direction.getStepX(), 0.0F, direction.getStepZ());
        Matrix4f flip = new Matrix4f()
            .translate(0.5F, 0.5F, 0.5F)
            .rotate(Axis.of(lengthwiseAxis).rotationDegrees(180.0F))
            .translate(-0.5F, -0.5F, -0.5F);

        return new Transformation(flip.mul(upright.getMatrix(), new Matrix4f()));
    }
}
