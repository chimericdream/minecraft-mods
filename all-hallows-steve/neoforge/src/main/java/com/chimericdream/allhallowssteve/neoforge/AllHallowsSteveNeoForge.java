package com.chimericdream.allhallowssteve.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

import com.chimericdream.allhallowssteve.AllHallowsSteveMod;
import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.neoforge.registry.LootModifierRegistry;

@Mod(ModInfo.MOD_ID)
public final class AllHallowsSteveNeoForge {
    public AllHallowsSteveNeoForge(@NotNull IEventBus bus) {
        AllHallowsSteveMod.init();

        LootModifierRegistry.LOOT_MODIFIERS.register(bus);

        // Common setup runs in parallel across mods, and postInit writes to vanilla's
        // non-thread-safe dispenser registry — enqueueWork runs it single-threaded afterwards.
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(AllHallowsSteveMod::postInit));
    }
}
