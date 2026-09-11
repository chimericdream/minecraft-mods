package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinBlockColors;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreen;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class AllHallowsSteveClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.CARVING_STATION_SCREEN_HANDLER.get(), CarvingStationScreen::new);

        ColorHandlerRegistry.registerBlockColors(DecoratedPumpkinBlockColors.TINT_SOURCE, ModBlocks.DECORATED_PUMPKIN.get());
    }
}
