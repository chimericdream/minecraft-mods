package com.chimericdream.shulkerstuff.client;

import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class ShulkerStuffClient {
    public static void onInitializeClient() {
        HandledScreens.register(ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), DyeStationScreen::new);
    }
}
