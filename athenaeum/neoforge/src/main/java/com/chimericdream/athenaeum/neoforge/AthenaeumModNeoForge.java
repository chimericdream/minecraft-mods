package com.chimericdream.athenaeum.neoforge;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.ModInfo;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class AthenaeumModNeoForge {
    public AthenaeumModNeoForge() {
        // Run our common setup.
        AthenaeumMod.init();
    }
}
