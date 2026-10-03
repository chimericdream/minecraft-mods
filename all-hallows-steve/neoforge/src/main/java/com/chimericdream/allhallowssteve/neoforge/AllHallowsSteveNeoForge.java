package com.chimericdream.allhallowssteve.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import com.chimericdream.allhallowssteve.AllHallowsSteveMod;
import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.client.PumpkinKeybindings;
import com.chimericdream.allhallowssteve.neoforge.registry.LootModifierRegistry;

@Mod(ModInfo.MOD_ID)
public final class AllHallowsSteveNeoForge {
    public AllHallowsSteveNeoForge(@NotNull IEventBus bus) {
        AllHallowsSteveMod.init();

        LootModifierRegistry.LOOT_MODIFIERS.register(bus);

        // Must run before construction finishes: architectury only hands key mappings to NeoForge from a
        // RegisterKeyMappingsEvent listener, and FMLClientSetupEvent fires after that event, so
        // the keybind never shows up in the Controls screen if registered there.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            PumpkinKeybindings.init();
        }

        // Common setup runs in parallel across mods, and postInit writes to vanilla's
        // non-thread-safe dispenser registry — enqueueWork runs it single-threaded afterwards.
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(AllHallowsSteveMod::postInit));
    }
}
