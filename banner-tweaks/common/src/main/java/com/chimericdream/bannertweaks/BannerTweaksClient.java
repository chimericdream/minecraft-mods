package com.chimericdream.bannertweaks;

import com.chimericdream.bannertweaks.network.ServerNetworking;

public class BannerTweaksClient {
    public static void onInitializeClient() {
        ServerNetworking.init();
    }
}
