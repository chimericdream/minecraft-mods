package com.chimericdream.sponj.neoforge;

import com.chimericdream.sponj.SponjMod;
import com.chimericdream.sponj.ModInfo;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class SponjNeoForge {
    public SponjNeoForge() {
        // Run our common setup.
        SponjMod.init();
    }
}
