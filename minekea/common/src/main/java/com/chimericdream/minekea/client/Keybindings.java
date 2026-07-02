package com.chimericdream.minekea.client;

import com.chimericdream.minekea.ModInfo;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class Keybindings {
    public static final KeyMapping CYCLE_PAINTER_COLOR = new KeyMapping(
        "key.minekea.items.painter.cycle_color",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_EQUALS,
        KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    static {
        KeyMappingRegistry.register(CYCLE_PAINTER_COLOR);
    }

    public static void init() {
    }
}
