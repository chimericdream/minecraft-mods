package com.chimericdream.opus.client;

import com.chimericdream.opus.ModInfo;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/** UNVERIFIED: never compiled. Copied from minekea's {@code Keybindings}. */
public class Keybindings {
    public static final KeyMapping OPEN_GUIDE = new KeyMapping(
        "key.opus.open_guide",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_F7,
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    static {
        KeyMappingRegistry.register(OPEN_GUIDE);
    }

    public static void init() {
    }
}
