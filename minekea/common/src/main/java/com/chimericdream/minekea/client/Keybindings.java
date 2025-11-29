package com.chimericdream.minekea.client;

import com.chimericdream.minekea.ModInfo;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Keybindings {
    public static final KeyBinding CYCLE_PAINTER_COLOR = new KeyBinding(
        "key.minekea.items.painter.cycle_color",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_EQUAL,
        KeyBinding.Category.create(Identifier.of(ModInfo.MOD_ID, "keybinds"))
    );

    static {
        KeyMappingRegistry.register(CYCLE_PAINTER_COLOR);
    }

    public static void init() {
    }
}
