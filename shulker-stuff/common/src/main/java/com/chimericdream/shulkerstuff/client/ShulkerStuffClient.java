package com.chimericdream.shulkerstuff.client;

import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class ShulkerStuffClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), DyeStationScreen::new);
    }
}
