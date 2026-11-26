package com.chimericdream.enchantingfixes.neoforge;

import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.client.EnchantingFixesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public final class EnchantingFixesNeoForgeClient {
    private EnchantingFixesNeoForgeClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EnchantingFixesClient.init();
    }
}
