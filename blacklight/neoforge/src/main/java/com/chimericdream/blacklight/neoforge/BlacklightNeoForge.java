package com.chimericdream.blacklight.neoforge;

import com.chimericdream.blacklight.BlacklightMod;
import com.chimericdream.blacklight.ModInfo;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class BlacklightNeoForge {
    public BlacklightNeoForge() {
        BlacklightMod.init();
    }
}
