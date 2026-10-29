package com.chimericdream.forwardingaddress.fabric.test.fixture;

import net.fabricmc.api.ModInitializer;

/**
 * {@code main} entrypoint of the {@code forwardingaddress_test} test mod. Only present in the
 * {@code gametest} source set, so it runs solely inside {@code runGameTest}.
 */
public class ForwardingAddressTestEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        TestFixtures.register();
    }
}
