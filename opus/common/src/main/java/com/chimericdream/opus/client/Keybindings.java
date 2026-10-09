package com.chimericdream.opus.client;

import com.chimericdream.opus.ModInfo;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/** Compiles against 26.2; not yet run in game. Copied from minekea's {@code Keybindings}. */
public class Keybindings {
    public static final KeyMapping OPEN_GUIDE = new KeyMapping(
        "key.opus.open_guide",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    static {
        KeyMappingRegistry.register(OPEN_GUIDE);
    }

    public static void init() {
    }
}
