package com.chimericdream.logallthethings.windowlog.client;

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

import com.chimericdream.logallthethings.windowlog.WindowedBlockEntity;

/**
 * Renders a window-logged slab/stair: the host renders as an ordinary block model via
 * {@link SubmitNodeCollector#submitMovingBlock} — the same vanilla mechanism
 * {@code PistonHeadRenderer} uses to render the block a piston is currently pushing. No custom baked
 * model or Forge-style per-instance model data is needed for the host: {@code windowed_block.json}
 * points every state at the empty {@code minecraft:block/air} model, and this renderer supplies the
 * real geometry every frame from the block entity's {@code hostState}.
 *
 * <p>The window itself prefers {@link WindowFrameRenderer}'s hand-authored, shape-fitted glass
 * geometry, falling back to the same {@code submitMovingBlock} treatment (a plain connected pane) only
 * when no frame model exists yet for that particular stair shape/half or for slabs.
 */
public class WindowedBlockEntityRenderer implements BlockEntityRenderer<WindowedBlockEntity, WindowedBlockRenderState> {
    public WindowedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull WindowedBlockRenderState createRenderState() {
        return new WindowedBlockRenderState();
    }

    @Override
    public void extractRenderState(
        WindowedBlockEntity blockEntity,
        WindowedBlockRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.host = null;
        state.window = null;

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        Holder<Biome> biome = level.getBiome(pos);

        if (!blockEntity.getHostState().isAir()) {
            state.host = createMovingBlock(pos, blockEntity.getHostState(), biome, level);
        }
        if (!blockEntity.getWindowState().isAir()) {
            state.window = createMovingBlock(pos, blockEntity.getWindowState(), biome, level);
        }
    }

    @Override
    public void submit(WindowedBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.host != null) {
            submitNodeCollector.submitMovingBlock(poseStack, state.host, 0);
        }
        if (state.window != null) {
            BlockState hostState = state.host != null ? state.host.blockState : Blocks.AIR.defaultBlockState();
            boolean renderedFrame = WindowFrameRenderer.submit(poseStack, submitNodeCollector, state.lightCoords, hostState, state.window.blockState);
            if (!renderedFrame) {
                submitNodeCollector.submitMovingBlock(poseStack, state.window, 0);
            }
        }
    }

    private static MovingBlockRenderState createMovingBlock(BlockPos pos, BlockState blockState, Holder<Biome> biome, ClientLevel level) {
        MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        movingBlockRenderState.randomSeedPos = pos;
        movingBlockRenderState.blockPos = pos;
        movingBlockRenderState.blockState = blockState;
        movingBlockRenderState.biome = biome;
        movingBlockRenderState.cardinalLighting = level.cardinalLighting();
        movingBlockRenderState.lightEngine = level.getLightEngine();
        return movingBlockRenderState;
    }
}
