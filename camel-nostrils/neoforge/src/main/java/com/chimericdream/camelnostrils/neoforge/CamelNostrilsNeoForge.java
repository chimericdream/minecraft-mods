package com.chimericdream.camelnostrils.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.camelnostrils.CamelNostrilsMod;
import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.client.CamelNostrilsClient;

@Mod(ModInfo.MOD_ID)
public final class CamelNostrilsNeoForge {
    public CamelNostrilsNeoForge() {
        CamelNostrilsMod.init();

        // Must run before construction finishes: architectury's own RegisterRenderers listener fires on
        // architectury's mod bus before ours, so any lifecycle event hook here is already too late.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            CamelNostrilsClient.registerEntityRenderers();
        }
    }
}
