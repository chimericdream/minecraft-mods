package com.chimericdream.bettertargetdummies.client;

import com.chimericdream.bettertargetdummies.client.screen.ModMenus;
import com.chimericdream.bettertargetdummies.client.screen.MobPickerScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class BetterTargetDummiesClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModMenus.MOB_PICKER_MENU.get(), MobPickerScreen::new);
    }
}
