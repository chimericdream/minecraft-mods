package com.chimericdream.artificialheart.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import com.chimericdream.artificialheart.ArtificialHeartMod;
import com.chimericdream.artificialheart.ModInfo;

@Mod(ModInfo.MOD_ID)
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public final class ArtificialHeartNeoForge {
    public ArtificialHeartNeoForge() {
        ArtificialHeartMod.init();
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        ArtificialHeartMod.postInit();
    }
}
