package com.chimericdream.polycraft.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.ModInfo;
import com.chimericdream.polycraft.client.PolyCraftClient;

@Mod(ModInfo.MOD_ID)
public final class PolyCraftNeoForge {
    public PolyCraftNeoForge() {
        PolyCraftMod.init();

        // Must run during construction, like the common init: Architectury registers its client event listeners
        // from mod-bus events that fire before FMLClientSetupEvent.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            PolyCraftClient.init();
        }
    }
}
