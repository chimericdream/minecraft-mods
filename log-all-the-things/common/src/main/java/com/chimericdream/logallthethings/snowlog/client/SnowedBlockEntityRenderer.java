package com.chimericdream.logallthethings.snowlog.client;

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
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.client.FaceLighting;
import com.chimericdream.logallthethings.client.QuadEmitter;
import com.chimericdream.logallthethings.client.RealNeighborMovingBlockRenderState;
import com.chimericdream.logallthethings.snowlog.SnowedBlockEntity;

/**
 * Renders a snow-logged host: the host renders as an ordinary block model via
 * {@link SubmitNodeCollector#submitMovingBlock}, same as
 * {@code carpetlog.client.CarpetedBlockEntityRenderer}. The snow portion differs per host, since
 * (unlike carpet's fixed 1px decal) it has real height:
 * <ul>
 *   <li>Walls/fences/chains/bars/panes and top slabs: vanilla's real snow blockstate, submitted
 *   unmodified - its own model is already floor-anchored (grows from y=0 up), which is exactly where
 *   the open space is for these hosts.</li>
 *   <li>Bottom slabs: the same real blockstate, translated up 0.5 to sit on the slab's own top
 *   surface.</li>
 *   <li>Straight stairs (both halves): a hand-cut half-footprint box, since vanilla's snow model is
 *   always full-footprint - see {@link SnowStairsRenderer}.</li>
 * </ul>
 */
public class SnowedBlockEntityRenderer implements BlockEntityRenderer<SnowedBlockEntity, SnowedBlockRenderState> {
    public SnowedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull SnowedBlockRenderState createRenderState() {
        return new SnowedBlockRenderState();
    }

    @Override
    public void extractRenderState(
        SnowedBlockEntity blockEntity,
        SnowedBlockRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.host = null;
        state.snow = null;
        state.snowState = Blocks.AIR.defaultBlockState();

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        Holder<Biome> biome = level.getBiome(pos);
        state.faceLight = FaceLighting.neighborLight(level, pos);
        state.cardinalLighting = level.cardinalLighting();

        BlockState hostState = blockEntity.getHostState();
        BlockState snowState = blockEntity.getSnowState();

        if (!hostState.isAir()) {
            state.host = createMovingBlock(pos, hostState, biome, level);
        }
        if (!snowState.isAir()) {
            state.snowState = snowState;
            if (!isStraightStairs(hostState)) {
                state.snow = createMovingBlock(pos, snowState, biome, level);
            }
        }
    }

    @Override
    public void submit(SnowedBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.host != null) {
            submitNodeCollector.submitMovingBlock(poseStack, state.host, 0);
        }

        if (state.snowState.isAir()) {
            return;
        }

        BlockState hostState = state.host != null ? state.host.blockState : Blocks.AIR.defaultBlockState();

        if (isStraightStairs(hostState)) {
            SnowStairsRenderer.submit(poseStack, submitNodeCollector, state.faceLight, state.cardinalLighting, hostState, state.snowState);
            return;
        }

        if (state.snow == null) {
            return;
        }

        boolean bottomSlab = hostState.getBlock() instanceof SlabBlock && hostState.getValue(SlabBlock.TYPE) != SlabType.TOP;
        if (bottomSlab) {
            // The offset snow stack's own bottom face lands exactly on the slab's own top face (both
            // at y=0.5) - see CarpetedBlockEntityRenderer#submit's fallback branch for why two
            // independently-submitted moving blocks (unlike ordinary chunk geometry) don't get culled
            // against each other and so z-fight at any coincident plane. Nudging the whole stack up by
            // QuadEmitter#SURFACE_NUDGE separates the planes by an imperceptible sliver instead.
            poseStack.pushPose();
            poseStack.translate(0.0, 0.5 + QuadEmitter.SURFACE_NUDGE, 0.0);
            submitNodeCollector.submitMovingBlock(poseStack, state.snow, 0);
            poseStack.popPose();
        } else {
            // A wall/fence/chain/bars/pane host (and a top slab, which shares this branch since its
            // open space is also unoffset from the floor) leaves the real snow blockstate's own bottom
            // face exactly coincident with the host's own bottom face at y=0 - the snow-logging
            // counterpart of CarpetedBlockEntityRenderer#submit's wall/fence/bars/pane fallback. Once a
            // wall/fence/bars/pane is connected on a side, that connection's arm also reaches the
            // block's edge, so the snow's own side faces at x/z=0/1 land exactly on that arm's own end
            // face too. Nudging the whole moving-block render down in Y and very slightly wider in X/Z
            // (scaled outward from the block's horizontal center) resolves both ties the same way
            // CarpetedBlockEntityRenderer's fallback does, at the same imperceptible
            // {@link QuadEmitter#SURFACE_NUDGE} magnitude.
            poseStack.pushPose();
            poseStack.translate(0.0, -QuadEmitter.SURFACE_NUDGE, 0.0);
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.scale(1.0F + 2.0F * QuadEmitter.SURFACE_NUDGE, 1.0F, 1.0F + 2.0F * QuadEmitter.SURFACE_NUDGE);
            poseStack.translate(-0.5, 0.0, -0.5);
            submitNodeCollector.submitMovingBlock(poseStack, state.snow, 0);
            poseStack.popPose();
        }
    }

    private static boolean isStraightStairs(BlockState hostState) {
        return hostState.getBlock() instanceof StairBlock && hostState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT;
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
