package com.chimericdream.effectivegear.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.effectivegear.EffectiveGearMod;
import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.client.Keybindings;
import com.chimericdream.effectivegear.neoforge.network.NeoForgeServerNetworking;

@Mod(ModInfo.MOD_ID)
public final class EffectiveGearNeoForge {
    public EffectiveGearNeoForge() {
        EffectiveGearMod.init();
        NeoForgeServerNetworking.init();

        // Must run before construction finishes: architectury only hands key mappings to NeoForge from a
        // RegisterKeyMappingsEvent listener, and FMLClientSetupEvent fires after that event, so
        // the keybind never shows up in the Controls screen if registered there.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            Keybindings.init();
        }
    }
}
