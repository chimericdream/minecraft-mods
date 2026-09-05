package com.chimericdream.flatbedrock.neoforge;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import com.chimericdream.flatbedrock.FlatBedrockMod;
import com.chimericdream.flatbedrock.config.FlatBedrockConfig;

@Mod(FlatBedrockMod.MOD_ID)
public final class FlatBedrockNeoForge {
    public FlatBedrockNeoForge() {
        // Run our common setup.
        FlatBedrockMod.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> FlatBedrockConfig.configScreen(parent)
        );
    }
}
