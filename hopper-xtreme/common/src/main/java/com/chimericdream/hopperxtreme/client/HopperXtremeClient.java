package com.chimericdream.hopperxtreme.client;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class HopperXtremeClient {
    public static void onInitializeClient() {
        HandledScreens.register(ModBlocks.GLAZED_HOPPER_SCREEN_HANDLER.get(), GlazedHopperScreen::new);
    }
}
