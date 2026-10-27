package com.chimericdream.forwardingaddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Forwarding Address's own settings, kept in a small JSON file instead of a YACL config so the mod
 * doesn't need YACL installed. Lives at {@code config/forwardingaddress/settings.json}.
 */
public final class ForwardingSettings {
    public static final int DEFAULT_MAX_CONVERSIONS_PER_TICK = 256;
    private static final int MAX_ALLOWED = 100_000;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ForwardingSettings current = new ForwardingSettings();

    /**
     * Most blocks (and block-entity containers) converted per level per server tick.
     */
    public int maxConversionsPerTick = DEFAULT_MAX_CONVERSIONS_PER_TICK;

    public static ForwardingSettings get() {
        return current;
    }

    public static Path folder() {
        return Platform.getConfigFolder().resolve(ModInfo.MOD_ID);
    }

    /**
     * Reads the settings file, writing the defaults if it doesn't exist. A file that can't be read
     * keeps the defaults and is left untouched, so a typo never overwrites someone's edits.
     */
    public static void load() {
        Path file = folder().resolve("settings.json");
        ForwardingSettings loaded = new ForwardingSettings();

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                ForwardingSettings parsed = GSON.fromJson(reader, ForwardingSettings.class);
                if (parsed != null) {
                    loaded = parsed;
                }
            } catch (IOException | RuntimeException e) {
                ForwardingAddressMod.LOGGER.error("Could not read {}; using the defaults", file, e);
            }
        } else {
            try {
                Files.createDirectories(file.getParent());
                try (Writer writer = Files.newBufferedWriter(file)) {
                    GSON.toJson(loaded, writer);
                }
            } catch (IOException e) {
                ForwardingAddressMod.LOGGER.warn("Could not write the default settings to {}", file, e);
            }
        }

        loaded.maxConversionsPerTick = clamp(loaded.maxConversionsPerTick);
        current = loaded;
    }

    static int clamp(int value) {
        return Math.max(1, Math.min(MAX_ALLOWED, value));
    }
}
