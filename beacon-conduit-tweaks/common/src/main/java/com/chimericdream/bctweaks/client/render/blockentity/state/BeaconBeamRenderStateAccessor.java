package com.chimericdream.bctweaks.client.render.blockentity.state;

import java.util.Set;

public interface BeaconBeamRenderStateAccessor {
    void bct$setHiddenSectionStarts(Set<Integer> hiddenSectionStarts);
    boolean bct$isSectionHidden(int beamStart);
}
