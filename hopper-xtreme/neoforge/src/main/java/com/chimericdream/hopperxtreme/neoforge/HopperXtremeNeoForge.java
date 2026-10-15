package com.chimericdream.hopperxtreme.neoforge;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.WrenchKeybindings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.hopperxtreme.HopperXtremeMod;

@Mod(ModInfo.MOD_ID)
public final class HopperXtremeNeoForge {
    public HopperXtremeNeoForge() {
        // Run our common setup.
        HopperXtremeMod.init();

        // Must run before construction finishes: architectury only hands key mappings to NeoForge from a
        // RegisterKeyMappingsEvent listener, and FMLClientSetupEvent fires after that event, so
        // the keybind never shows up in the Controls screen if registered there.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            WrenchKeybindings.init();
        }
    }
}
