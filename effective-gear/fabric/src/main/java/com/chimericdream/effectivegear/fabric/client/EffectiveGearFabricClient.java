package com.chimericdream.effectivegear.fabric.client;

import com.chimericdream.effectivegear.client.Keybindings;
import com.chimericdream.effectivegear.client.PreservingBlockColors;
import com.chimericdream.effectivegear.network.UseAbilityPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class EffectiveGearFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Keybindings.init();
        PreservingBlockColors.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Keybindings.USE_ABILITY.consumeClick()) {
                ClientPlayNetworking.send(UseAbilityPayload.INSTANCE);
            }
        });
    }
}
