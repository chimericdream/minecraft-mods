package com.chimericdream.stackitup.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 * Migrates legacy AllStackable config files (this mod's predecessor) into StackItUp's own config
 * files. Deliberately has no dependency on Minecraft/Architectury/StackItUp singletons so it can be
 * unit-tested with plain JUnit.
 */
public final class ConfigMigration {
    public enum Result {
        MIGRATED,
        NOT_FOUND,
        CORRUPTED,
    }

    public static final class Outcome {
        public final Result result;
        public final ArrayList<LinkedHashMap<String, Integer>> data;

        private Outcome(Result result, ArrayList<LinkedHashMap<String, Integer>> data) {
            this.result = result;
            this.data = data;
        }

        static Outcome of(Result result) {
            return new Outcome(result, null);
        }

        static Outcome migrated(ArrayList<LinkedHashMap<String, Integer>> data) {
            return new Outcome(Result.MIGRATED, data);
        }
    }

    private ConfigMigration() {
    }

    /**
     * Derives the legacy AllStackable file that corresponds to a given StackItUp config file, e.g.
     * {@code stackitup-config.json -> allstackable-config.json} or
     * {@code stackitup-global-config.json -> allstackable-global-config.json}.
     */
    public static File legacyFileFor(File newFile) {
        String legacyName = newFile.getName().replaceFirst("^stackitup", "allstackable");
        return new File(newFile.getParentFile(), legacyName);
    }

    /**
     * Attempts to migrate the legacy AllStackable config sitting next to {@code newFile}. Callers
     * should only invoke this when {@code newFile} does not already exist. On success (or on finding
     * a corrupted legacy file), the legacy file and its {@code .bk} backup are deleted.
     */
    public static Outcome migrate(File newFile, Gson gson) {
        File legacyFile = legacyFileFor(newFile);

        if (!legacyFile.exists()) {
            return Outcome.of(Result.NOT_FOUND);
        }

        ArrayList<LinkedHashMap<String, Integer>> data = null;
        try (FileReader reader = new FileReader(legacyFile)) {
            data = gson.fromJson(reader, new TypeToken<ArrayList<LinkedHashMap<String, Integer>>>() {
            }.getType());
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            data = null;
        }

        if (data == null || data.size() != 2) {
            deleteQuietly(legacyFile);
            return Outcome.of(Result.CORRUPTED);
        }

        deleteQuietly(legacyFile);
        return Outcome.migrated(data);
    }

    private static void deleteQuietly(File legacyFile) {
        try {
            legacyFile.delete();
            File legacyBackup = new File(legacyFile.getAbsolutePath() + ".bk");
            if (legacyBackup.exists()) {
                legacyBackup.delete();
            }
        } catch (SecurityException ignored) {
            // Best-effort cleanup; a leftover legacy file is harmless.
        }
    }
}
