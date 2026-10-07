package com.chimericdream.opus.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.client.OpusClient;

@Mod(ModInfo.MOD_ID)
public final class OpusNeoForge {
    public OpusNeoForge() {
        OpusMod.init();

        // Must run during construction: architectury registers its reload listeners and key mappings from mod-bus
        // events that fire before FMLClientSetupEvent, so registering from that event is too late and the
        // books never load.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            OpusClient.init();
        }
    }
}
