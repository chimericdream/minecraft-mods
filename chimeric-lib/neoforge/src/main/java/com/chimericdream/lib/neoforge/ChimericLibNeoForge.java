package com.chimericdream.lib.neoforge;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.commands.PlatformCommandArgumentTypes;
import com.chimericdream.lib.neoforge.commands.PlatformCommandArgumentTypesImpl;
import net.neoforged.fml.common.Mod;

@Mod(ChimericLib.MOD_ID)
public final class ChimericLibNeoForge {
    public ChimericLibNeoForge() {
        // Platform providers must be wired up before ChimericLib.init() runs, since it registers
        // things (like custom command argument types) that need them.
        PlatformCommandArgumentTypes.setProvider(new PlatformCommandArgumentTypesImpl());

        // Run our common setup.
        ChimericLib.init();
    }
}
