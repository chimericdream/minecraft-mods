package com.chimericdream.lib.neoforge.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.architectury.registry.registries.DeferredRegister;

import java.util.Locale;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.commands.PlatformCommandArgumentTypes;

public final class PlatformCommandArgumentTypesImpl implements PlatformCommandArgumentTypes.Provider {
    @Override
    @SuppressWarnings("unchecked")
    public <A extends ArgumentType<?>> void registerByClass(Class<A> clazz, ArgumentTypeInfo<A, ?> info) {
        ArgumentTypeInfos.registerByClass(clazz, info);

        DeferredRegister<ArgumentTypeInfo<?, ?>> argumentTypes = DeferredRegister.create(
            ChimericLib.MOD_ID, (ResourceKey<Registry<ArgumentTypeInfo<?, ?>>>) BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key()
        );
        argumentTypes.register(clazz.getSimpleName().toLowerCase(Locale.ROOT), () -> info);
        argumentTypes.register();
    }
}
