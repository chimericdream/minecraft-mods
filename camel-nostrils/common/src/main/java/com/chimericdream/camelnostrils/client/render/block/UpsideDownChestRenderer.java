package com.chimericdream.camelnostrils.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class UpsideDownChestRenderer<T extends BlockEntity & LidBlockEntity> extends ChestRenderer<T> {
    public UpsideDownChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(@NonNull T blockEntity, @NonNull ChestRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        // The 180-degree flip in submit() mirrors left/right along with top/bottom, so the half that
        // joins on its right edge in world space needs the model built to join on its left, and vice
        // versa - swap the type the model gets picked by to compensate.
        state.type = switch (state.type) {
            case LEFT -> ChestType.RIGHT;
            case RIGHT -> ChestType.LEFT;
            case SINGLE -> ChestType.SINGLE;
        };
    }

    @Override
    public void submit(@NonNull ChestRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.5, -0.5, -0.5);

        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.popPose();
    }
}
