package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.network.CyclePumpkinFacePayload;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * The key that turns a worn decorated pumpkin to its next carved face (north, east, south, west). Unbound
 * by default, since the pumpkin is a novelty that shouldn't take a key someone already uses. Pressing it
 * only does anything while a decorated pumpkin is on the player's head; the server checks that again.
 */
public final class PumpkinKeybindings {
    public static final KeyMapping CYCLE_PUMPKIN_FACE = new KeyMapping(
        "key.allhallowssteve.cycle_pumpkin_face",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "keybinds"))
    );

    private PumpkinKeybindings() {
    }

    public static void init() {
        KeyMappingRegistry.register(CYCLE_PUMPKIN_FACE);

        ClientTickEvent.CLIENT_POST.register(PumpkinKeybindings::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        WornPumpkinOverlay.tick(minecraft);

        while (CYCLE_PUMPKIN_FACE.consumeClick()) {
            if (minecraft.player != null && PumpkinFaces.isWearable(minecraft.player.getItemBySlot(EquipmentSlot.HEAD))) {
                NetworkManager.sendToServer(CyclePumpkinFacePayload.INSTANCE);
            }
        }
    }
}
