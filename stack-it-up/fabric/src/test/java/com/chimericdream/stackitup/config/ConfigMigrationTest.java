package com.chimericdream.stackitup.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pure file/JSON logic - no Minecraft dependency, so no bootstrap is needed.
class ConfigMigrationTest {
    private final Gson gson = new Gson();

    @Test
    void legacyFileForDerivesPerWorldConfigName(@TempDir File dir) {
        File newFile = new File(dir, "stackitup-config.json");

        assertEquals("allstackable-config.json", ConfigMigration.legacyFileFor(newFile).getName());
    }

    @Test
    void legacyFileForDerivesGlobalConfigName(@TempDir File dir) {
        File newFile = new File(dir, "stackitup-global-config.json");

        assertEquals("allstackable-global-config.json", ConfigMigration.legacyFileFor(newFile).getName());
    }

    @Test
    void migrateReturnsNotFoundWhenNoLegacyFileExists(@TempDir File dir) {
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.NOT_FOUND, outcome.result);
        assertNull(outcome.data);
    }

    @Test
    void migrateReadsAndDeletesAValidLegacyFile(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "[{\"minecraft:cobblestone\":128},{\"permissionLevel\":4,\"stackEmptyShulkerBoxOnly\":0}]");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.MIGRATED, outcome.result);
        assertEquals(2, outcome.data.size());
        assertEquals(Integer.valueOf(128), outcome.data.get(0).get("minecraft:cobblestone"));
        assertEquals(Integer.valueOf(4), outcome.data.get(1).get("permissionLevel"));
        assertFalse(legacyFile.exists());
    }

    @Test
    void migrateDeletesTheLegacyBackupFileToo(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "[{},{\"permissionLevel\":4,\"stackEmptyShulkerBoxOnly\":0}]");
        File legacyBackup = new File(dir, "allstackable-config.json.bk");
        writeJson(legacyBackup, "[{},{\"permissionLevel\":4,\"stackEmptyShulkerBoxOnly\":0}]");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.migrate(newFile, gson);

        assertFalse(legacyFile.exists());
        assertFalse(legacyBackup.exists());
    }

    @Test
    void migrateHandlesAMissingLegacyBackupFileGracefully(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "[{},{\"permissionLevel\":4,\"stackEmptyShulkerBoxOnly\":0}]");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.MIGRATED, outcome.result);
    }

    @Test
    void migrateTreatsInvalidJsonAsCorruptedAndDeletesTheLegacyFile(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "not valid json");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.CORRUPTED, outcome.result);
        assertFalse(legacyFile.exists());
    }

    @Test
    void migrateTreatsWrongShapeAsCorruptedAndDeletesTheLegacyFile(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "[{\"permissionLevel\":4}]");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.CORRUPTED, outcome.result);
        assertFalse(legacyFile.exists());
    }

    @Test
    void migrateTreatsNullJsonAsCorruptedAndDeletesTheLegacyFile(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-config.json");
        writeJson(legacyFile, "null");
        File newFile = new File(dir, "stackitup-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.CORRUPTED, outcome.result);
        assertFalse(legacyFile.exists());
    }

    @Test
    void migrateOnGlobalConfigUsesTheGlobalLegacyFileName(@TempDir File dir) throws IOException {
        File legacyFile = new File(dir, "allstackable-global-config.json");
        writeJson(legacyFile, "[{},{\"applyGlobalConfigToAllNewGames\":0,\"permissionLevel\":4,\"stackEmptyShulkerBoxOnly\":0}]");
        File newFile = new File(dir, "stackitup-global-config.json");

        ConfigMigration.Outcome outcome = ConfigMigration.migrate(newFile, gson);

        assertEquals(ConfigMigration.Result.MIGRATED, outcome.result);
        assertTrue(outcome.data.get(1).containsKey("applyGlobalConfigToAllNewGames"));
    }

    private static void writeJson(File file, String contents) throws IOException {
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
    }
}
