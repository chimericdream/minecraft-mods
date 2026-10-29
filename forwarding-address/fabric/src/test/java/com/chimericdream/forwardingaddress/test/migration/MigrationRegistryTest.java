package com.chimericdream.forwardingaddress.test.migration;

import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.ItemMigration;
import com.chimericdream.forwardingaddress.migration.MigrationLog;
import com.chimericdream.forwardingaddress.migration.MigrationRegistry;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Resolving IDs goes through BuiltInRegistries, so it bootstraps Minecraft.
public class MigrationRegistryTest extends BootstrapMinecraft {
    private static Identifier id(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    private static BlockMigration block(String from, String to) {
        return new BlockMigration(id(from), id(to), null, null, null, null);
    }

    private static MigrationRegistry registry() {
        return new MigrationRegistry(new MigrationLog(LogManager.getLogger("forwardingaddress-test")));
    }

    @Test
    void startsEmpty() {
        MigrationRegistry registry = registry();

        assertTrue(registry.isEmpty());
        assertFalse(registry.hasBlockMigrations());
        assertFalse(registry.hasItemMigrations());
    }

    @Test
    void resolvesAMigrationToTheRegisteredBlocks() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "birch_stairs"));

        MigrationRegistry.ResolvedBlock resolved = registry.forBlock(Blocks.OAK_STAIRS);

        assertNotNull(resolved);
        assertSame(Blocks.OAK_STAIRS, resolved.from());
        assertSame(Blocks.BIRCH_STAIRS, resolved.to());
        assertTrue(registry.isMigratedBlock(Blocks.OAK_STAIRS));
        assertFalse(registry.isMigratedBlock(Blocks.BIRCH_STAIRS));
    }

    @Test
    void aBlockMigrationAlsoConvertsTheBlocksItem() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "birch_stairs"));

        MigrationRegistry.ResolvedItem resolved = registry.forItem(Items.OAK_STAIRS);

        assertNotNull(resolved);
        assertSame(Items.BIRCH_STAIRS, resolved.to());
    }

    @Test
    void anExplicitItemMigrationBeatsTheOneImpliedByTheBlock() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "birch_stairs"));
        registry.register(new ItemMigration(id("oak_stairs"), id("spruce_stairs"), null));

        assertSame(Items.SPRUCE_STAIRS, registry.forItem(Items.OAK_STAIRS).to());
    }

    @Test
    void aMissingTargetIsSkippedNotFatal() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "not_installed"));

        assertNull(registry.forBlock(Blocks.OAK_STAIRS));
        assertNull(registry.forItem(Items.OAK_STAIRS));
    }

    @Test
    void aMissingOldBlockIsIgnored() {
        MigrationRegistry registry = registry();
        registry.register(block("long_gone", "birch_stairs"));

        assertFalse(registry.isMigratedBlock(Blocks.BIRCH_STAIRS));
    }

    @Test
    void aSecondMigrationForTheSameBlockIsIgnored() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "birch_stairs"));
        registry.register(block("oak_stairs", "spruce_stairs"));

        assertSame(Blocks.BIRCH_STAIRS, registry.forBlock(Blocks.OAK_STAIRS).to());
    }

    @Test
    void registeringAfterTheFirstLookupStillTakesEffect() {
        MigrationRegistry registry = registry();
        registry.register(block("oak_stairs", "birch_stairs"));
        assertNull(registry.forBlock(Blocks.SPRUCE_STAIRS));

        registry.register(block("spruce_stairs", "birch_stairs"));

        assertNotNull(registry.forBlock(Blocks.SPRUCE_STAIRS));
        assertEquals(Blocks.BIRCH_STAIRS, registry.forBlock(Blocks.SPRUCE_STAIRS).to());
    }
}
