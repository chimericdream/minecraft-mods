package com.chimericdream.bctweaks;

import net.minecraft.world.level.block.entity.BeaconBeamOwner;

public interface BeaconAccessor {
    void bct$addRange(double d);
    void bct$resetRange();
    double bct$getRange();

    boolean bct$shouldIgnoreTintedGlass();
    void bct$ignoreNextTintedGlass();
    void bct$stopIgnoringTintedGlass();

    boolean bct$isBeamHidden();
    void bct$setBeamHidden(boolean hidden);

    void bct$appendBeamSection(BeaconBeamOwner.Section section);
}
