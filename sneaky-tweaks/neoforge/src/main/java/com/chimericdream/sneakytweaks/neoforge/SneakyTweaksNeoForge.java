package com.chimericdream.sneakytweaks.neoforge;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import com.chimericdream.sneakytweaks.SneakyTweaksMod;
import com.chimericdream.sneakytweaks.ModInfo;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;

@Mod(ModInfo.MOD_ID)
public final class SneakyTweaksNeoForge {
    public SneakyTweaksNeoForge() {
        SneakyTweaksMod.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> SneakyTweaksConfig.configScreen(parent)
        );
    }
}
