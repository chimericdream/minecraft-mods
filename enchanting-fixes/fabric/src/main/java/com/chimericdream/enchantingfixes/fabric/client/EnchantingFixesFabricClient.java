package com.chimericdream.enchantingfixes.fabric.client;

import com.chimericdream.enchantingfixes.client.EnchantingFixesClient;
import net.fabricmc.api.ClientModInitializer;

public final class EnchantingFixesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EnchantingFixesClient.init();
    }
}
