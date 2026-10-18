package com.chimericdream.pannotiacompanion.fabric.test;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.pannotiacompanion.home.Cooldowns;
import com.chimericdream.pannotiacompanion.home.Home;
import com.chimericdream.pannotiacompanion.home.HomeBook;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeBookTest extends BootstrapMinecraft {
    private static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:overworld"));
    private static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:the_nether"));

    private static Home home(String name, ResourceKey<Level> dimension) {
        return new Home(name, dimension, 1, 2, 3, 0, 0);
    }

    @Test
    void limitIsPerDimension() {
        HomeBook book = new HomeBook();

        assertEquals(HomeBook.SetResult.ADDED, book.set(home("a", OVERWORLD), 1));
        assertEquals(HomeBook.SetResult.LIMIT_REACHED, book.set(home("b", OVERWORLD), 1));
        assertEquals(HomeBook.SetResult.ADDED, book.set(home("b", NETHER), 1));
        assertEquals(2, book.homes().size());
    }

    @Test
    void reusingANameReplacesInsteadOfCountingAgainstTheLimit() {
        HomeBook book = new HomeBook();
        book.set(home("a", OVERWORLD), 1);

        assertEquals(HomeBook.SetResult.REPLACED, book.set(new Home("A", OVERWORLD, 9, 9, 9, 0, 0), 1));
        assertEquals(1, book.homes().size());
        assertEquals(9, book.resolve("a").orElseThrow().x());
    }

    @Test
    void movingAHomeIntoAFullDimensionIsRejected() {
        HomeBook book = new HomeBook();
        book.set(home("a", OVERWORLD), 1);
        book.set(home("b", NETHER), 1);

        assertEquals(HomeBook.SetResult.LIMIT_REACHED, book.set(home("a", NETHER), 1));
        assertEquals(OVERWORLD, book.resolve("a").orElseThrow().dimension());
    }

    @Test
    void resolvesByNameThenByOneBasedId() {
        HomeBook book = new HomeBook();
        book.set(home("base", OVERWORLD), 5);
        book.set(home("mine", OVERWORLD), 5);

        assertEquals("mine", book.resolve("MINE").orElseThrow().name());
        assertEquals("base", book.resolve("1").orElseThrow().name());
        assertEquals("mine", book.resolve("2").orElseThrow().name());
        assertTrue(book.resolve("3").isEmpty());
        assertTrue(book.resolve("0").isEmpty());
    }

    @Test
    void defaultHomeIsTheOneNamedHomeOrTheOnlyOne() {
        HomeBook book = new HomeBook();
        assertTrue(book.resolveDefault().isEmpty());

        book.set(home("base", OVERWORLD), 5);
        assertEquals("base", book.resolveDefault().orElseThrow().name());

        book.set(home("mine", OVERWORLD), 5);
        assertTrue(book.resolveDefault().isEmpty());

        book.set(home("home", NETHER), 5);
        assertEquals("home", book.resolveDefault().orElseThrow().name());
    }

    @Test
    void renameRejectsMissingAndTakenNames() {
        HomeBook book = new HomeBook();
        book.set(home("a", OVERWORLD), 5);
        book.set(home("b", OVERWORLD), 5);

        assertEquals(HomeBook.RenameResult.NOT_FOUND, book.rename("zzz", "c"));
        assertEquals(HomeBook.RenameResult.NAME_TAKEN, book.rename("a", "B"));
        assertEquals(HomeBook.RenameResult.RENAMED, book.rename("a", "c"));
        assertTrue(book.resolve("c").isPresent());
        assertTrue(book.resolve("a").isEmpty());
    }

    @Test
    void removeByNameOrId() {
        HomeBook book = new HomeBook();
        book.set(home("a", OVERWORLD), 5);
        book.set(home("b", OVERWORLD), 5);

        assertEquals("b", book.remove("2").orElseThrow().name());
        assertEquals("a", book.remove("a").orElseThrow().name());
        assertTrue(book.isEmpty());
        assertTrue(book.remove("a").isEmpty());
    }

    @Test
    void nameValidation() {
        assertFalse(HomeBook.isValidName(" "));
        assertFalse(HomeBook.isValidName("x".repeat(HomeBook.MAX_NAME_LENGTH + 1)));
        assertTrue(HomeBook.isValidName("my base"));
    }

    @Test
    void cooldownCountsDownAndRoundsUp() {
        Cooldowns cooldowns = new Cooldowns();
        UUID player = UUID.randomUUID();

        assertEquals(0, cooldowns.remainingSeconds(player, 0));

        cooldowns.start(player, 100, 10);
        assertEquals(10, cooldowns.remainingSeconds(player, 100));
        assertEquals(10, cooldowns.remainingSeconds(player, 119));
        assertEquals(9, cooldowns.remainingSeconds(player, 120));
        assertEquals(0, cooldowns.remainingSeconds(player, 300));
    }

    @Test
    void zeroSecondCooldownNeverBlocks() {
        Cooldowns cooldowns = new Cooldowns();
        UUID player = UUID.randomUUID();

        cooldowns.start(player, 0, 0);
        assertEquals(0, cooldowns.remainingSeconds(player, 0));
    }
}
