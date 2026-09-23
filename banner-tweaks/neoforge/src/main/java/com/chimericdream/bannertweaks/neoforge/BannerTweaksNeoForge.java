package com.chimericdream.bannertweaks.neoforge;

import com.chimericdream.bannertweaks.BannerTweaksMod;
import com.chimericdream.bannertweaks.ModInfo;
import com.chimericdream.bannertweaks.neoforge.network.NeoForgeServerNetworking;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class BannerTweaksNeoForge {
    public BannerTweaksNeoForge() {
        BannerTweaksMod.init();
        NeoForgeServerNetworking.init();
    }
}
