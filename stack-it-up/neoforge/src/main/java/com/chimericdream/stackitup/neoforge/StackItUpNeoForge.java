package com.chimericdream.stackitup.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import com.chimericdream.stackitup.StackItUpMod;
import com.chimericdream.stackitup.ModInfo;

@Mod(ModInfo.MOD_ID)
public final class StackItUpNeoForge {
    public StackItUpNeoForge(@NotNull IEventBus bus) {
        StackItUpMod.init();
    }
}
