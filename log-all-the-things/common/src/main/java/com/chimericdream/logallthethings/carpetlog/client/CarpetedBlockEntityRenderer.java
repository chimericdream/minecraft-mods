package com.chimericdream.logallthethings.carpetlog.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.carpetlog.CarpetedBlockEntity;
import com.chimericdream.logallthethings.client.FaceLighting;
import com.chimericdream.logallthethings.client.RealNeighborMovingBlockRenderState;

/**
 * Renders a carpet-logged slab/stair: the host renders as an ordinary block model via
 * {@link SubmitNodeCollector#submitMovingBlock}, and the carpet prefers {@link CarpetFrameRenderer}'s
 * hand-authored, shape-fitted overlay, falling back to the same {@code submitMovingBlock} treatment (a
 * plain flat carpet) only when no overlay model exists yet for that host shape. Mirrors
 * {@code windowlog.client.WindowedBlockEntityRenderer}.
 */
public class CarpetedBlockEntityRenderer implements BlockEntityRenderer<CarpetedBlockEntity, CarpetedBlockRenderState> {
    public CarpetedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull CarpetedBlockRenderState createRenderState() {
        return new CarpetedBlockRenderState();
    }

    @Override
    public void extractRenderState(
        CarpetedBlockEntity blockEntity,
        CarpetedBlockRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.host = null;
        state.carpet = null;

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        Holder<Biome> biome = level.getBiome(pos);
        state.faceLight = FaceLighting.neighborLight(level, pos);
        state.cardinalLighting = level.cardinalLighting();

        if (!blockEntity.getHostState().isAir()) {
            state.host = createMovingBlock(pos, blockEntity.getHostState(), biome, level);
        }
        if (!blockEntity.getCarpetState().isAir()) {
            state.carpet = createMovingBlock(pos, blockEntity.getCarpetState(), biome, level);
        }
    }

    @Override
    public void submit(CarpetedBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.host != null) {
            submitNodeCollector.submitMovingBlock(poseStack, state.host, 0);
        }
        if (state.carpet != null) {
            BlockState hostState = state.host != null ? state.host.blockState : Blocks.AIR.defaultBlockState();
            boolean renderedFrame = CarpetFrameRenderer.submit(poseStack, submitNodeCollector, state.faceLight, state.cardinalLighting, hostState, state.carpet.blockState);
            if (!renderedFrame) {
                // A wall/fence/bars/pane host has no shape-fitted overlay (see CarpetFrameRenderer's
                // select()), so this renders the carpet as its own real, unmodified block model. That
                // model's own bottom face (y=0) coincides with a connected host's own bottom face the
                // same way a slab/stair's fitted overlay coincides with its host's top face - and once
                // a wall/fence/bars/pane is connected on a side, that connection's arm reaches all the
                // way to the block's edge, so the carpet's own side faces at x/z=0/1 land exactly on
                // that arm's own outer end face too. {@code CarpetFrameRenderer} resolves the same kind
                // of tie per-vertex, along each vertex's own face normal; a whole real block model here
                // isn't individual vertices this renderer controls, so this nudges the whole moving-block
                // render instead - down in Y for the bottom-face case, and very slightly wider in X/Z
                // (scaled outward from the block's horizontal center) for the side-face case. Both use
                // the same {@link CarpetFrameRenderer#SURFACE_NUDGE} magnitude so the effect stays exactly
                // as imperceptible as the per-vertex version.
                poseStack.pushPose();
                poseStack.translate(0.0, -CarpetFrameRenderer.SURFACE_NUDGE, 0.0);
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(1.0F + 2.0F * CarpetFrameRenderer.SURFACE_NUDGE, 1.0F, 1.0F + 2.0F * CarpetFrameRenderer.SURFACE_NUDGE);
                poseStack.translate(-0.5, 0.0, -0.5);
                submitNodeCollector.submitMovingBlock(poseStack, state.carpet, 0);
                poseStack.popPose();
            }
        }
    }

    private static MovingBlockRenderState createMovingBlock(BlockPos pos, BlockState blockState, Holder<Biome> biome, ClientLevel level) {
        MovingBlockRenderState movingBlockRenderState = new RealNeighborMovingBlockRenderState(level);
        movingBlockRenderState.randomSeedPos = pos;
        movingBlockRenderState.blockPos = pos;
        movingBlockRenderState.blockState = blockState;
        movingBlockRenderState.biome = biome;
        movingBlockRenderState.cardinalLighting = level.cardinalLighting();
        movingBlockRenderState.lightEngine = level.getLightEngine();
        return movingBlockRenderState;
    }
}
