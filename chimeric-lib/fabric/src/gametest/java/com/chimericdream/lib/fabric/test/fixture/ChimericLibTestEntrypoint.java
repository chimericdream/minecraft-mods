package com.chimericdream.lib.fabric.test.fixture;

import net.fabricmc.api.ModInitializer;

/**
 * {@code main} entrypoint of the {@code chimericlib_test} test mod. Only present in the {@code gametest}
 * source set, so it runs solely inside {@code runGameTest} — this is what keeps the fixture content out
 * of the shipped jar.
 */
public class ChimericLibTestEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        TestFixtures.register();
    }
}
