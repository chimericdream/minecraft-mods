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
                submitNodeCollector.submitMovingBlock(poseStack, state.carpet, 0);
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
