package com.chimericdream.lib.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

/**
 * Registers a custom Brigadier {@link ArgumentType} with whatever platform-specific mechanism makes
 * it usable in a command that gets synced to the client (vanilla's own reverse class-to-info lookup
 * used for that sync is private, and populating it needs Fabric API's
 * {@code ArgumentTypeRegistry}/NeoForge's patched {@code ArgumentTypeInfos.registerByClass}
 * respectively). Not {@code @ExpectPlatform}: that annotation needs its generated {@code Impl} class
 * in this same package on the platform source set, but NeoForge's dev run resolves common and the
 * neoforge source set as separate JPMS modules there, which fails FML startup — see
 * {@code CampfireGraceHolder} in sneaky-tweaks for the prior incident.
 */
public final class PlatformCommandArgumentTypes {
    private static Provider provider;

    private PlatformCommandArgumentTypes() {
    }

    public static void setProvider(Provider platformProvider) {
        provider = platformProvider;
    }

    public static <A extends ArgumentType<?>> void registerByClass(Class<A> clazz, ArgumentTypeInfo<A, ?> info) {
        provider.registerByClass(clazz, info);
    }

    public interface Provider {
        <A extends ArgumentType<?>> void registerByClass(Class<A> clazz, ArgumentTypeInfo<A, ?> info);
    }
}
