package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.BeaconSectionAccessor;
import com.chimericdream.bctweaks.client.render.blockentity.state.BeaconBeamRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(BeaconRenderer.class)
public abstract class BCTweaksBeaconRendererMixin {
    /**
     * Mirrors {@code BeaconRenderer.submit}'s own cumulative-height bookkeeping so the redirect below
     * can tell, purely from a section's start offset, whether that section was marked hidden by
     * {@code BCTweaksBeaconMixin.bct$maybeStartHiddenSection}.
     */
    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BeaconRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At("TAIL"))
    private void bct$markHiddenBeamSections(BlockEntity blockEntity, BeaconRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        Set<Integer> hiddenStarts = new HashSet<>();
        int cumulativeHeight = 0;

        for (BeaconBeamOwner.Section section : ((BeaconBeamOwner) blockEntity).getBeamSections()) {
            if (((BeaconSectionAccessor) section).bct$isHidden()) {
                hiddenStarts.add(cumulativeHeight);
            }

            cumulativeHeight += section.getHeight();
        }

        ((BeaconBeamRenderStateAccessor) state).bct$setHiddenSectionStarts(hiddenStarts);
    }

    @Redirect(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BeaconRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;submitBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;FFIII)V"))
    private void bct$skipHiddenBeamSection(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, float beamRadiusScale, float animationTime, int beamStart, int height, int color, BeaconRenderState state) {
        if (((BeaconBeamRenderStateAccessor) state).bct$isSectionHidden(beamStart)) {
            return;
        }

        BeaconRenderer.submitBeaconBeam(poseStack, submitNodeCollector, BeaconRenderer.BEAM_LOCATION, 1.0F, animationTime, beamStart, height, color, BeaconRenderer.SOLID_BEAM_RADIUS * beamRadiusScale, BeaconRenderer.BEAM_GLOW_RADIUS * beamRadiusScale);
    }
}
