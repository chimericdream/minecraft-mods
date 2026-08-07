package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.client.render.blockentity.state.BeaconBeamRenderStateAccessor;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(BeaconRenderState.class)
public class BCTweaksBeaconRenderStateMixin implements BeaconBeamRenderStateAccessor {
    @Unique
    private Set<Integer> bct$hiddenSectionStarts = Set.of();

    @Override
    public void bct$setHiddenSectionStarts(Set<Integer> hiddenSectionStarts) {
        bct$hiddenSectionStarts = hiddenSectionStarts;
    }

    @Override
    public boolean bct$isSectionHidden(int beamStart) {
        return bct$hiddenSectionStarts.contains(beamStart);
    }
}
