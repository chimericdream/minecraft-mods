package com.chimericdream.rulesforthee.test.rule;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.rulesforthee.data.PlayerEntry;
import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.EffectiveRules;
import com.chimericdream.rulesforthee.rule.RuleOverrides;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.gamerules.GameRules;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectiveRulesTest extends BootstrapMinecraft {
    private static final UUID KID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ADULT = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void playerWithNothingGetsTheWorldValue() {
        RulesData data = new RulesData();

        assertFalse(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
        assertTrue(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, true));
    }

    @Test
    void ownOverrideBeatsTheWorld() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);

        assertTrue(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
        // The override is per player: someone else still gets the world value.
        assertFalse(EffectiveRules.resolve(data, ADULT, GameRules.KEEP_INVENTORY, false));
    }

    @Test
    void overrideCanBeStricterThanTheWorld() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.FALL_DAMAGE, false);

        assertFalse(EffectiveRules.resolve(data, KID, GameRules.FALL_DAMAGE, true));
    }

    @Test
    void profileBeatsTheWorldAndOwnOverrideBeatsTheProfile() {
        RulesData data = new RulesData();
        assertTrue(data.createProfile("kids"));
        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);
        data.profile("kids").orElseThrow().set(GameRules.FALL_DAMAGE, false);
        data.entry(KID).setProfile(Optional.of("kids"));

        assertTrue(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
        assertFalse(EffectiveRules.resolve(data, KID, GameRules.FALL_DAMAGE, true));

        data.entry(KID).overrides().set(GameRules.FALL_DAMAGE, true);
        assertTrue(EffectiveRules.resolve(data, KID, GameRules.FALL_DAMAGE, false));
        // The profile's other values still apply.
        assertTrue(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
    }

    @Test
    void integerRulesRoundTripThroughTheOverrides() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.RESPAWN_RADIUS, 3);

        assertEquals(3, EffectiveRules.resolve(data, KID, GameRules.RESPAWN_RADIUS, 10));
        assertEquals(10, EffectiveRules.resolve(data, ADULT, GameRules.RESPAWN_RADIUS, 10));
    }

    @Test
    void settingOneRuleDoesNotAffectAnother() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, true);

        assertTrue(EffectiveRules.override(data, KID, GameRules.FALL_DAMAGE).isEmpty());
    }

    @Test
    void clearingAnOverrideFallsBackToTheProfileThenTheWorld() {
        RulesData data = new RulesData();
        data.createProfile("kids");
        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);
        data.entry(KID).setProfile(Optional.of("kids"));
        data.entry(KID).overrides().set(GameRules.KEEP_INVENTORY, false);

        assertFalse(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, true));

        assertTrue(data.entry(KID).overrides().clear(GameRules.KEEP_INVENTORY));
        assertTrue(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
    }

    @Test
    void deletingAProfileTakesItOffItsMembers() {
        RulesData data = new RulesData();
        data.createProfile("kids");
        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);
        data.entry(KID).setProfile(Optional.of("kids"));

        assertTrue(data.deleteProfile("kids"));

        assertTrue(data.entry(KID).profile().isEmpty());
        assertFalse(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
        assertFalse(data.deleteProfile("kids"));
    }

    @Test
    void aMissingProfileIsIgnored() {
        RulesData data = new RulesData();
        data.entry(KID).setProfile(Optional.of("gone"));

        assertFalse(EffectiveRules.resolve(data, KID, GameRules.KEEP_INVENTORY, false));
    }

    @Test
    void aPlayerHasAtMostOneProfileInV1() {
        PlayerEntry entry = new PlayerEntry();
        entry.setProfile(Optional.of("a"));
        entry.setProfile(Optional.of("b"));

        assertEquals(1, entry.profiles().size());
        assertEquals(Optional.of("b"), entry.profile());

        entry.setProfile(Optional.empty());
        assertTrue(entry.profiles().isEmpty());
    }

    @Test
    void profileNamesAreValidated() {
        RulesData data = new RulesData();

        assertTrue(data.createProfile("kids_2"));
        assertFalse(data.createProfile("kids_2"), "duplicate");
        assertFalse(data.createProfile("Kids"), "uppercase");
        assertFalse(data.createProfile("has space"));
        assertFalse(data.createProfile(""));
    }

    @Test
    void effectiveMergesTheProfileUnderTheOwnOverrides() {
        RulesData data = new RulesData();
        data.createProfile("kids");
        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);
        data.profile("kids").orElseThrow().set(GameRules.FALL_DAMAGE, false);
        data.entry(KID).setProfile(Optional.of("kids"));
        data.entry(KID).overrides().set(GameRules.FALL_DAMAGE, true);

        RuleOverrides effective = EffectiveRules.effective(data, KID);

        assertEquals(Optional.of(true), effective.get(GameRules.KEEP_INVENTORY));
        assertEquals(Optional.of(true), effective.get(GameRules.FALL_DAMAGE));
        assertTrue(EffectiveRules.effective(data, ADULT).isEmpty());
    }

    @Test
    void canonicalFormIsIndependentOfTheOrderRulesWereSet() {
        RuleOverrides a = new RuleOverrides();
        a.set(GameRules.KEEP_INVENTORY, true);
        a.set(GameRules.FALL_DAMAGE, false);
        RuleOverrides b = new RuleOverrides();
        b.set(GameRules.FALL_DAMAGE, false);
        b.set(GameRules.KEEP_INVENTORY, true);

        assertEquals(a.canonical(), b.canonical());

        b.set(GameRules.FALL_DAMAGE, true);
        assertFalse(a.canonical().equals(b.canonical()));
    }

    @Test
    void unparsableOrUnknownStoredValuesAreIgnored() {
        RulesData data = new RulesData();
        data.entry(KID).overrides().set(GameRules.RESPAWN_RADIUS, 5);

        // Round-trip through the codec with a value that no longer parses and a key for a removed rule.
        JsonElement json = RulesData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow();
        String text = json.toString()
            .replace("\"minecraft:respawn_radius\":\"5\"", "\"minecraft:respawn_radius\":\"nope\",\"minecraft:gone\":\"1\"");
        JsonElement edited = com.google.gson.JsonParser.parseString(text);
        RulesData loaded = RulesData.CODEC.parse(JsonOps.INSTANCE, edited).getOrThrow();

        assertEquals(77, EffectiveRules.resolve(loaded, KID, GameRules.RESPAWN_RADIUS, 77));
    }

    @Test
    void savedDataSurvivesACodecRoundTrip() {
        RulesData data = new RulesData();
        data.createProfile("kids");
        data.profile("kids").orElseThrow().set(GameRules.KEEP_INVENTORY, true);
        data.entry(KID).setProfile(Optional.of("kids"));
        data.entry(KID).overrides().set(GameRules.RESPAWN_RADIUS, 4);
        data.entry(KID).setLastKnownName("Junior");
        data.entry(KID).setLastNotified("x");

        DataResult<Tag> encoded = RulesData.CODEC.encodeStart(NbtOps.INSTANCE, data);
        RulesData loaded = RulesData.CODEC.parse(NbtOps.INSTANCE, encoded.getOrThrow()).getOrThrow();

        assertTrue(EffectiveRules.resolve(loaded, KID, GameRules.KEEP_INVENTORY, false));
        assertEquals(4, EffectiveRules.resolve(loaded, KID, GameRules.RESPAWN_RADIUS, 10));
        assertEquals("Junior", loaded.entry(KID).lastKnownName());
        assertEquals("x", loaded.entry(KID).lastNotified());
        assertEquals(Optional.of("kids"), loaded.entry(KID).profile());
        assertTrue(loaded.profileNames().contains("kids"));
    }
}
