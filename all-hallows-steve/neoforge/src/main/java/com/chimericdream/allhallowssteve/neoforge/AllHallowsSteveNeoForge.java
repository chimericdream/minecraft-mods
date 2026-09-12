package com.chimericdream.allhallowssteve.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import com.chimericdream.allhallowssteve.AllHallowsSteveMod;
import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.neoforge.registry.LootModifierRegistry;

@Mod(ModInfo.MOD_ID)
public final class AllHallowsSteveNeoForge {
    public AllHallowsSteveNeoForge(@NotNull IEventBus bus) {
        AllHallowsSteveMod.init();

        LootModifierRegistry.LOOT_MODIFIERS.register(bus);
    }
}
