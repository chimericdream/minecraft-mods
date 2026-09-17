package com.chimericdream.lib.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Common-code registry of {@link YaclConfig}s that want a config screen, keyed by
 * {@link YaclConfig#modId()}. {@link YaclConfig#init()} is the only expected writer — see its javadoc,
 * and {@link YaclConfig}'s class javadoc, for why registration happens here instead of each mod handing
 * a screen factory straight to Mod Menu/NeoForge.
 *
 * <p>The two platform-specific classes that actually wire screens up to Mod Menu/NeoForge —
 * {@code com.chimericdream.lib.fabric.config.YaclModMenuIntegration} and
 * {@code com.chimericdream.lib.neoforge.config.YaclConfigScreensNeoForge} — read {@link #all()} at the
 * point their respective platform asks for screens/extension points, which is always after every mod's
 * constructor/{@code ModInitializer} has run.
 *
 * <p>This class is kept public (not package-private) so tests, and any other caller with a legitimate
 * reason, can register/query it directly without going through {@link YaclConfig#init()}.
 *
 * <p><b>Thread-safety:</b> NeoForge can construct mods in parallel, so {@link #register(YaclConfig)}
 * may be called concurrently from multiple threads. The backing list is a {@link CopyOnWriteArrayList},
 * and the read-remove-add sequence in {@link #register(YaclConfig)} is synchronized so two concurrent
 * registrations for different mod ids never race each other's insert, and a re-registration for the
 * same mod id can't interleave into two entries.
 */
public final class YaclConfigScreens {
    private static final List<YaclConfig<?>> CONFIGS = new CopyOnWriteArrayList<>();

    private YaclConfigScreens() {
    }

    /**
     * Registers {@code config}, keyed by {@link YaclConfig#modId()}. If a config was already registered
     * for that mod id, it is replaced (in place of its old position) rather than duplicated — so
     * calling this (or {@link YaclConfig#init()}) more than once for the same mod id is safe.
     */
    public static void register(YaclConfig<?> config) {
        synchronized (CONFIGS) {
            int existingIndex = -1;

            for (int i = 0; i < CONFIGS.size(); i++) {
                if (CONFIGS.get(i).modId().equals(config.modId())) {
                    existingIndex = i;
                    break;
                }
            }

            if (existingIndex >= 0) {
                CONFIGS.set(existingIndex, config);
            } else {
                CONFIGS.add(config);
            }
        }
    }

    /** An unmodifiable snapshot of every registered config, in insertion order. */
    public static List<YaclConfig<?>> all() {
        return Collections.unmodifiableList(new ArrayList<>(CONFIGS));
    }

    /** The registered config for {@code modId}, if any. */
    public static Optional<YaclConfig<?>> forMod(String modId) {
        return CONFIGS.stream()
            .filter(config -> config.modId().equals(modId))
            .findFirst();
    }
}
