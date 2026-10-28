package com.chimericdream.forwardingaddress.stub;

import net.minecraft.resources.Identifier;

import java.util.function.Predicate;

/**
 * Decides whether a stub may be registered under an ID. The rule is "is the owning mod installed", not
 * registration order, because the installed-mod list is known before anything registers on both
 * loaders. That makes the answer the same whatever order the mods load in.
 */
public final class StubGuard {
    public enum Decision {
        /**
         * The ID is free: register the stub.
         */
        REGISTER,
        /**
         * The mod that owns the namespace is installed, so its real block is used and no stub is needed.
         */
        MOD_INSTALLED,
        /**
         * Stubs under {@code minecraft:} are never allowed.
         */
        REFUSED_VANILLA,
        /**
         * Something is already registered under the ID even though its mod isn't installed. Registering
         * would crash, so the caller stops with a clear message instead.
         */
        ALREADY_REGISTERED
    }

    private StubGuard() {
    }

    public static Decision check(Identifier id, Predicate<String> modInstalled, Predicate<Identifier> blockRegistered) {
        if (id.getNamespace().equals("minecraft")) {
            return Decision.REFUSED_VANILLA;
        }

        if (modInstalled.test(id.getNamespace())) {
            return Decision.MOD_INSTALLED;
        }

        if (blockRegistered.test(id)) {
            return Decision.ALREADY_REGISTERED;
        }

        return Decision.REGISTER;
    }
}
