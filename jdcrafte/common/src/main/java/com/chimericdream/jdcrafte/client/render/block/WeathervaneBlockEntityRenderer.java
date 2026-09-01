package com.chimericdream.jdcrafte.client.render.block;

import com.chimericdream.jdcrafte.block.WeathervaneBlock;
import com.chimericdream.jdcrafte.block.WeathervaneBlockEntity;
import com.chimericdream.jdcrafte.client.RealNeighborMovingBlockRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders the weathervane by submitting its own (unrotated) block model as a moving block, then
 * applying the {@link WeathervaneBlock#ROTATION} step as a runtime Y-axis pose rotation - see
 * {@link WeathervaneBlock}'s class doc for why this can't be done with a baked model instead.
 */
public class WeathervaneBlockEntityRenderer implements BlockEntityRenderer<WeathervaneBlockEntity, WeathervaneBlockRenderState> {
    public WeathervaneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull WeathervaneBlockRenderState createRenderState() {
        return new WeathervaneBlockRenderState();
    }

    @Override
    public void extractRenderState(
        WeathervaneBlockEntity blockEntity,
        WeathervaneBlockRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.movingBlock = null;

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();

        state.rotation = blockState.getValue(WeathervaneBlock.ROTATION);

        MovingBlockRenderState movingBlock = new RealNeighborMovingBlockRenderState(level);
        Holder<Biome> biome = level.getBiome(pos);

        movingBlock.randomSeedPos = pos;
        movingBlock.blockPos = pos;
        movingBlock.blockState = blockState;
        movingBlock.biome = biome;
        movingBlock.cardinalLighting = level.cardinalLighting();
        movingBlock.lightEngine = level.getLightEngine();

        state.movingBlock = movingBlock;
    }

    @Override
    public void submit(WeathervaneBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState) {
        if (state.movingBlock == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation * 45.0F));
        poseStack.translate(-0.5, 0.0, -0.5);

        submitNodeCollector.submitMovingBlock(poseStack, state.movingBlock, 0);

        poseStack.popPose();
    }
}
