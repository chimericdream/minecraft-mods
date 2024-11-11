package com.chimericdream.athenaeum.neoforge;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.ModInfo;
import com.chimericdream.athenaeum.config.AthenaeumConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModInfo.MOD_ID)
public final class AthenaeumModNeoForge {
    public AthenaeumModNeoForge() {
        // Run our common setup.
        AthenaeumMod.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> AthenaeumConfig.configScreen(parent)
        );
    }
}
