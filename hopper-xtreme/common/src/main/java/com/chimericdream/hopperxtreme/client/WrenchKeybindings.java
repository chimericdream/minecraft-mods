package com.chimericdream.hopperxtreme.client;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.WrenchItem;
import com.chimericdream.hopperxtreme.network.CycleWrenchModePayload;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

/**
 * The key that switches the Hopper Wrench between Rotate and Copy / Paste. Pressing it only does anything while a
 * Hopper Wrench is in either hand; the server checks that again.
 */
public final class WrenchKeybindings {
    public static final KeyMapping CYCLE_WRENCH_MODE = new KeyMapping(
        "key.hopperxtreme.wrench.cycle_mode",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_M,
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    private WrenchKeybindings() {
    }

    public static void init() {
        KeyMappingRegistry.register(CYCLE_WRENCH_MODE);

        ClientTickEvent.CLIENT_POST.register(WrenchKeybindings::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        while (CYCLE_WRENCH_MODE.consumeClick()) {
            if (minecraft.player != null && holdingWrench(minecraft)) {
                NetworkManager.sendToServer(CycleWrenchModePayload.INSTANCE);
            }
        }
    }

    private static boolean holdingWrench(Minecraft minecraft) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (minecraft.player.getItemInHand(hand).getItem() instanceof WrenchItem) {
                return true;
            }
        }

        return false;
    }
}
