package com.chimericdream.lib.neoforge;

import com.chimericdream.lib.ChimericLib;
import net.neoforged.fml.common.Mod;

@Mod(ChimericLib.MOD_ID)
public final class ChimericLibNeoForge {
    public ChimericLibNeoForge() {
        // Run our common setup.
        ChimericLib.init();
    }
}
