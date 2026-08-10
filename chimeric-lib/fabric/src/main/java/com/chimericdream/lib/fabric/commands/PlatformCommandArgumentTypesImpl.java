package com.chimericdream.lib.fabric.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

import java.util.Locale;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.commands.PlatformCommandArgumentTypes;

public final class PlatformCommandArgumentTypesImpl implements PlatformCommandArgumentTypes.Provider {
    @Override
    public <A extends ArgumentType<?>> void registerByClass(Class<A> clazz, ArgumentTypeInfo<A, ?> info) {
        Identifier id = Identifier.fromNamespaceAndPath(ChimericLib.MOD_ID, clazz.getSimpleName().toLowerCase(Locale.ROOT));
        ArgumentTypeRegistry.registerArgumentType(id, clazz, info);
    }
}
