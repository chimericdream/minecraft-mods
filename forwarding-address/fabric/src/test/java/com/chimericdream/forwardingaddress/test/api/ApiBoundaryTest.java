package com.chimericdream.forwardingaddress.test.api;

import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.ItemMigration;
import com.chimericdream.forwardingaddress.api.MigrationBackend;
import com.chimericdream.forwardingaddress.api.Migrations;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiBoundaryTest {
    private static final Path API_SOURCE = Path.of("..", "common", "src", "main", "java", "com", "chimericdream", "forwardingaddress", "api");
    private static final String MOD_PACKAGE = "com.chimericdream.forwardingaddress.";
    private static final String API_PACKAGE = "com.chimericdream.forwardingaddress.api";

    /**
     * The API package is meant to be splittable into its own artifact, which only works while nothing
     * in it reaches into the rest of the mod.
     */
    @Test
    void apiPackageNeverImportsTheRestOfTheMod() throws IOException {
        List<String> violations = new ArrayList<>();

        try (Stream<Path> files = Files.list(API_SOURCE)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                for (String line : Files.readAllLines(file)) {
                    String trimmed = line.trim();
                    boolean reachesOut = trimmed.startsWith("import " + MOD_PACKAGE) && !trimmed.startsWith("import " + API_PACKAGE + ".");
                    if (reachesOut || trimmed.contains(MOD_PACKAGE + "migration")) {
                        violations.add(file.getFileName() + ": " + trimmed);
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "api must not depend on the rest of the mod, but found: " + violations);
    }

    @Test
    void theBuilderHandsATidyMigrationToTheBackend() {
        List<BlockMigration> blocks = new ArrayList<>();
        List<ItemMigration> items = new ArrayList<>();
        Migrations.installBackend(new MigrationBackend() {
            @Override
            public void register(BlockMigration migration) {
                blocks.add(migration);
            }

            @Override
            public void register(ItemMigration migration) {
                items.add(migration);
            }
        });

        Identifier from = Identifier.fromNamespaceAndPath("oldmod", "trim");
        Identifier to = Identifier.fromNamespaceAndPath("newmod", "trim");

        Migrations.block(from).to(to).register();
        Migrations.item(from).to(to).mapItem((old, converted) -> converted).register();

        assertEquals(1, blocks.size());
        assertEquals(from, blocks.get(0).from());
        assertEquals(to, blocks.get(0).to());
        assertNull(blocks.get(0).propertyMapper());
        assertNull(blocks.get(0).entityMapper());
        assertNull(blocks.get(0).hiddenSlots());

        assertEquals(1, items.size());
        assertNotNull(items.get(0).mapper());
    }

    /**
     * Mod load order isn't guaranteed, so a consumer can declare before Forwarding Address starts.
     * That has to work, and the declarations must arrive in order once the backend appears.
     */
    @Test
    void declarationsMadeBeforeTheBackendExistsAreDeliveredInOrder() {
        Migrations.reset();
        try {
            Identifier first = Identifier.fromNamespaceAndPath("early", "first");
            Identifier second = Identifier.fromNamespaceAndPath("early", "second");
            Identifier third = Identifier.fromNamespaceAndPath("late", "third");
            Identifier target = Identifier.fromNamespaceAndPath("newmod", "target");

            Migrations.block(first).to(target).register();
            Migrations.item(second).to(target).register();
            assertEquals(2, Migrations.waitingCount());

            List<Identifier> received = new ArrayList<>();
            Migrations.installBackend(new MigrationBackend() {
                @Override
                public void register(BlockMigration migration) {
                    received.add(migration.from());
                }

                @Override
                public void register(ItemMigration migration) {
                    received.add(migration.from());
                }
            });

            assertEquals(List.of(first, second), received);
            assertEquals(0, Migrations.waitingCount());

            Migrations.block(third).to(target).register();
            assertEquals(List.of(first, second, third), received);
        } finally {
            Migrations.reset();
        }
    }
}
