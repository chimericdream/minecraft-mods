package com.chimericdream.allhallowssteve.client.render;

import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.LitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a placed decorated pumpkin's carved stencil overlays. The pumpkin's own tinted cube renders
 * normally through the vanilla block-model path ({@link DecoratedPumpkinBlock} keeps the default
 * {@code RenderShape.MODEL}) — this renderer only adds the stencil decals on top, via
 * {@link DecoratedPumpkinStencilRenderer}.
 */
public class DecoratedPumpkinBlockEntityRenderer implements BlockEntityRenderer<DecoratedPumpkinBlockEntity, DecoratedPumpkinRenderState> {
    public DecoratedPumpkinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull DecoratedPumpkinRenderState createRenderState() {
        return new DecoratedPumpkinRenderState();
    }

    @Override
    public void extractRenderState(
        DecoratedPumpkinBlockEntity blockEntity,
        DecoratedPumpkinRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = blockEntity.getBlockState().getValue(DecoratedPumpkinBlock.FACING);
        state.stencils = blockEntity.getStencils();
        state.cardinalLighting = blockEntity.getLevel() instanceof ClientLevel level ? level.cardinalLighting() : CardinalLighting.DEFAULT;

        Block block = blockEntity.getBlockState().getBlock();
        state.overlaySuffix = block instanceof LitDecoratedPumpkinBlock lit ? lit.overlaySuffix : "";
    }

    @Override
    public void submit(DecoratedPumpkinRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        DecoratedPumpkinStencilRenderer.submit(poseStack, submitNodeCollector, state.facing, state.stencils, state.cardinalLighting, state.lightCoords, state.overlaySuffix);
    }
}
