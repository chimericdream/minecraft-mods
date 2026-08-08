package com.chimericdream.butwhatabout.fabric.client;

import com.chimericdream.butwhatabout.client.ButWhatAboutClient;
import net.fabricmc.api.ClientModInitializer;

public class ButWhatAboutFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ButWhatAboutClient.onInitializeClient();
    }
}
