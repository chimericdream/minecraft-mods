package com.chimericdream.effectivegear.neoforge;

import net.neoforged.fml.common.Mod;

import com.chimericdream.effectivegear.EffectiveGearMod;
import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.neoforge.network.NeoForgeServerNetworking;

@Mod(ModInfo.MOD_ID)
public final class EffectiveGearNeoForge {
    public EffectiveGearNeoForge() {
        EffectiveGearMod.init();
        NeoForgeServerNetworking.init();
    }
}
