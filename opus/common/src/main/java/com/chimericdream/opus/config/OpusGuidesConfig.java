package com.chimericdream.opus.config;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.item.OpusBookItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server-side settings for {@code /opus give}, stored in {@code config/opus-guides.json}. The file is re-read
 * whenever its modification time changes, so edits apply without a restart. A missing or invalid file (or
 * field) falls back to the defaults with a warning, never an error.
 */
public final class OpusGuidesConfig {
    public static final String FILE_NAME = "opus-guides.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static long loadedModified = Long.MIN_VALUE;
    private static boolean opsOnly = false;
    private static String defaultGuide = OpusBookItem.DEFAULT_BOOK;

    private OpusGuidesConfig() {
    }

    public static synchronized boolean opsOnly() {
        reloadIfChanged();
        return opsOnly;
    }

    public static synchronized String defaultGuide() {
        reloadIfChanged();
        return defaultGuide;
    }

    /** Forces the next read to reload the file (used by tests that rewrite it within the same timestamp tick). */
    public static synchronized void invalidate() {
        loadedModified = Long.MIN_VALUE;
    }

    public static Path path() {
        return Platform.getConfigFolder().resolve(FILE_NAME);
    }

    private static void reloadIfChanged() {
        Path file = path();
        long modified;
        try {
            modified = Files.exists(file) ? Files.getLastModifiedTime(file).toMillis() : -1L;
        } catch (IOException e) {
            modified = -1L;
        }

        if (modified == loadedModified) {
            return;
        }
        loadedModified = modified;
        opsOnly = false;
        defaultGuide = OpusBookItem.DEFAULT_BOOK;

        if (modified == -1L) {
            writeDefaults(file);
            return;
        }

        try {
            JsonObject json = GSON.fromJson(Files.readString(file), JsonObject.class);
            if (json == null) {
                return;
            }
            if (json.has("opsOnly")) {
                opsOnly = json.get("opsOnly").getAsBoolean();
            }
            if (json.has("defaultGuide")) {
                String id = json.get("defaultGuide").getAsString();
                if (Identifier.tryParse(id) != null) {
                    defaultGuide = id;
                } else {
                    OpusMod.LOGGER.warn("{}: defaultGuide '{}' is not a valid id; using {}", FILE_NAME, id, OpusBookItem.DEFAULT_BOOK);
                }
            }
        } catch (IOException | RuntimeException e) {
            opsOnly = false;
            defaultGuide = OpusBookItem.DEFAULT_BOOK;
            OpusMod.LOGGER.warn("Could not read {}; using defaults ({})", FILE_NAME, e.toString());
        }
    }

    private static void writeDefaults(Path file) {
        JsonObject json = new JsonObject();
        json.addProperty("opsOnly", false);
        json.addProperty("defaultGuide", OpusBookItem.DEFAULT_BOOK);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(json) + "\n");
            loadedModified = Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            OpusMod.LOGGER.warn("Could not write default {} ({})", FILE_NAME, e.toString());
        }
    }
}
