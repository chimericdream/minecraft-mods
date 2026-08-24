package com.chimericdream.effectivegear.neoforge.client;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.client.Keybindings;
import com.chimericdream.effectivegear.network.UseAbilityPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class EffectiveGearNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Keybindings.init();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (Keybindings.USE_ABILITY.consumeClick()) {
            ClientPacketDistributor.sendToServer(UseAbilityPayload.INSTANCE);
        }
    }
}
