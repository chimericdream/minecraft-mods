package com.chimericdream.nextupdatenow.neoforge;

import com.chimericdream.nextupdatenow.ModInfo;
import com.chimericdream.nextupdatenow.NextUpdateNowMod;
import com.chimericdream.nextupdatenow.client.NextUpdateNowClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(ModInfo.MOD_ID)
public final class NextUpdateNowNeoForge {
    public NextUpdateNowNeoForge() {
        NextUpdateNowMod.init();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NextUpdateNowClient.registerEntityRenderers();
        }
    }
}
