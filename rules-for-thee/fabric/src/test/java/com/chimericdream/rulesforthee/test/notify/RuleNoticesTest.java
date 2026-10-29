package com.chimericdream.rulesforthee.test.notify;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.notify.RuleNotices;
import com.chimericdream.rulesforthee.rule.RuleOverrides;
import com.chimericdream.rulesforthee.rule.SupportedRules;
import net.minecraft.world.level.gamerules.GameRules;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleNoticesTest extends BootstrapMinecraft {
    private static final UUID KID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void aPlayerWithNoRulesIsNeverNotified() {
        RulesData data = new RulesData();

        assertTrue(RuleNotices.takePending(data, KID, "Junior").isEmpty());
        // And asking didn't create an entry for them.
        assertTrue(data.playerEntry(KID).isEmpty());
    }

    @Test
    void aNewOverrideIsReportedOnceThenQuiet() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);

        Optional<List<String>> first = RuleNotices.takePending(data, KID, "Junior");

        assertTrue(first.isPresent());
        assertTrue(first.get().stream().anyMatch(line -> line.contains("keep_inventory = true")), first.get().toString());
        assertTrue(RuleNotices.takePending(data, KID, "Junior").isEmpty());
    }

    @Test
    void aChangeWhileOfflineIsReportedOnNextCall() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);
        RuleNotices.takePending(data, KID, "Junior");

        // The operator changes the rule while the player is away.
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, false);

        Optional<List<String>> next = RuleNotices.takePending(data, KID, "Junior");
        assertTrue(next.isPresent());
        assertTrue(next.get().stream().anyMatch(line -> line.contains("keep_inventory = false")));
    }

    @Test
    void removingEveryRuleTellsThePlayerOnce() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);
        RuleNotices.takePending(data, KID, "Junior");

        data.entry(KID).overrides().clear(GameRules.KEEP_INVENTORY);

        Optional<List<String>> removed = RuleNotices.takePending(data, KID, "Junior");
        assertTrue(removed.isPresent());
        assertTrue(removed.get().getFirst().contains("removed"));
        assertTrue(RuleNotices.takePending(data, KID, "Junior").isEmpty());
    }

    @Test
    void changingAProfileChangesTheMembersNotice() {
        RulesData data = new RulesData();
        data.createProfile("kids");
        data.entry(KID).setProfile(Optional.of("kids"));
        assertTrue(RuleNotices.takePending(data, KID, "Junior").isEmpty(), "an empty profile changes nothing");

        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);

        assertTrue(RuleNotices.takePending(data, KID, "Junior").isPresent());
    }

    @Test
    void recordsTheLastKnownName() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);

        RuleNotices.takePending(data, KID, "Junior");

        assertEquals("Junior", data.entry(KID).lastKnownName());
    }

    @Test
    void shortNameDropsTheNamespace() {
        assertEquals("keep_inventory", RuleNotices.shortName("minecraft:keep_inventory"));
        assertEquals("plain", RuleNotices.shortName("plain"));
    }

    @Test
    void describeListsEachRule() {
        RuleOverrides rules = new RuleOverrides();
        rules.set(GameRules.KEEP_INVENTORY, true);
        rules.set(GameRules.RESPAWN_RADIUS, 3);

        List<String> lines = RuleNotices.describe(rules);

        assertEquals(3, lines.size());
    }

    @Test
    void supportedRulesAreFoundByTheirCommandName() {
        assertEquals("keep_inventory", SupportedRules.nameOf(GameRules.KEEP_INVENTORY));
        assertTrue(SupportedRules.find("keep_inventory").isPresent());
        assertTrue(SupportedRules.isSupported(GameRules.KEEP_INVENTORY));
        assertFalse(SupportedRules.isSupported(GameRules.PVP));
        assertTrue(SupportedRules.find("nope").isEmpty());
    }
}
