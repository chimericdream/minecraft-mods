package com.chimericdream.villagertweaks.neoforge;

import com.chimericdream.villagertweaks.ModInfo;
import com.chimericdream.villagertweaks.VillagerTweaksMod;
import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModInfo.MOD_ID)
public final class VillagerTweaksNeoForge {
    public VillagerTweaksNeoForge() {
        VillagerTweaksMod.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> VillagerTweaksConfig.configScreen(parent)
        );
    }
}
