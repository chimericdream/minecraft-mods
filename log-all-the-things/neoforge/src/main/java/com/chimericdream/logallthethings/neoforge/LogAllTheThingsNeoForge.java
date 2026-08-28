package com.chimericdream.logallthethings.neoforge;

import net.neoforged.fml.common.Mod;

import com.chimericdream.logallthethings.LogAllTheThingsMod;
import com.chimericdream.logallthethings.ModInfo;
import com.chimericdream.logallthethings.lavalog.LavaLogFlammability;
import com.chimericdream.logallthethings.neoforge.lavalog.LavaLogFlammabilityImpl;

@Mod(ModInfo.MOD_ID)
public final class LogAllTheThingsNeoForge {
    public LogAllTheThingsNeoForge() {
        LogAllTheThingsMod.init();

        LavaLogFlammability.setProvider(new LavaLogFlammabilityImpl());
    }
}
