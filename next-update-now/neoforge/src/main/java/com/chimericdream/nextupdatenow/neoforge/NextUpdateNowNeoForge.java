package com.chimericdream.nextupdatenow.neoforge;

import net.neoforged.fml.common.Mod;

import com.chimericdream.nextupdatenow.NextUpdateNowMod;
import com.chimericdream.nextupdatenow.ModInfo;

@Mod(ModInfo.MOD_ID)
public final class NextUpdateNowNeoForge {
    public NextUpdateNowNeoForge() {
        NextUpdateNowMod.init();
    }
}
