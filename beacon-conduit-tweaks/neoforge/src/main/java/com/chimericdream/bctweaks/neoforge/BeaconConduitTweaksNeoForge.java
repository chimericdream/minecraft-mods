package com.chimericdream.bctweaks.neoforge;

import com.chimericdream.bctweaks.BeaconConduitTweaksMod;
import com.chimericdream.bctweaks.ModInfo;
import com.chimericdream.bctweaks.config.BCTweaksConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModInfo.MOD_ID)
public final class BeaconConduitTweaksNeoForge {
    public BeaconConduitTweaksNeoForge() {
        // Run our common setup.
        BeaconConduitTweaksMod.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> BCTweaksConfig.configScreen(parent)
        );
    }
}
