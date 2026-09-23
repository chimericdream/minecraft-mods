package com.chimericdream.bctweaks.neoforge;

import com.chimericdream.bctweaks.BeaconConduitTweaksMod;
import com.chimericdream.bctweaks.ModInfo;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class BeaconConduitTweaksNeoForge {
    public BeaconConduitTweaksNeoForge() {
        // Run our common setup.
        BeaconConduitTweaksMod.init();
    }
}
