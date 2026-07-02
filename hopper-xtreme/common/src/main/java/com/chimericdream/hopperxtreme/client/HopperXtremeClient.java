package com.chimericdream.hopperxtreme.client;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.client.screen.FilteredGlazedHopperScreen;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreen;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreen;
import com.chimericdream.hopperxtreme.client.screen.HopperItemFilterScreen;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class HopperXtremeClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.FILTERED_HOPPER_SCREEN_HANDLER.get(), FilteredHopperScreen::new);
        MenuScreens.register(ModBlocks.FILTERED_GLAZED_HOPPER_SCREEN_HANDLER.get(), FilteredGlazedHopperScreen::new);
        MenuScreens.register(ModBlocks.GLAZED_HOPPER_SCREEN_HANDLER.get(), GlazedHopperScreen::new);
        MenuScreens.register(ModItems.HOPPER_ITEM_FILTER_SCREEN_HANDLER.get(), HopperItemFilterScreen::new);
    }
}
