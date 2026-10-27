package com.chimericdream.forwardingaddress.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;

public final class ForwardingAddressFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForwardingAddressMod.init();
    }
}
