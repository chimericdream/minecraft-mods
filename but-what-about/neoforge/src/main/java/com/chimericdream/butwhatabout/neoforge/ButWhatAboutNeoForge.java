package com.chimericdream.butwhatabout.neoforge;

import com.chimericdream.butwhatabout.ButWhatAboutMod;
import com.chimericdream.butwhatabout.ModInfo;
import net.neoforged.fml.common.Mod;

@Mod(ModInfo.MOD_ID)
public final class ButWhatAboutNeoForge {
    public ButWhatAboutNeoForge() {
        // Run our common setup.
        ButWhatAboutMod.init();
    }
}
