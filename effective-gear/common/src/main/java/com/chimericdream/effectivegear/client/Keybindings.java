package com.chimericdream.effectivegear.client;

import com.chimericdream.effectivegear.ModInfo;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybindings {
    public static final KeyMapping USE_ABILITY = new KeyMapping(
        "key.effectivegear.abilities.use",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_R,
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    static {
        KeyMappingRegistry.register(USE_ABILITY);
    }

    public static void init() {
    }
}
