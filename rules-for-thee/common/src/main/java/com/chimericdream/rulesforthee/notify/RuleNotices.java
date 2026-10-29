package com.chimericdream.rulesforthee.notify;

import com.chimericdream.rulesforthee.data.PlayerEntry;
import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.EffectiveRules;
import com.chimericdream.rulesforthee.rule.RuleOverrides;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Decides what a player should be told about their rules. This holds only the decision and the wording,
 * with no server types, so it can be unit tested; {@link RuleNotifier} sends the result.
 *
 * <p>A player is told whenever their effective rules differ from what they were last told. That covers
 * both "changed while online" (told at once) and "changed while offline" (told on their next login),
 * since the last-told rules are stored with the player.
 */
public final class RuleNotices {
    private RuleNotices() {
    }

    /**
     * What to tell the player now, or empty if they've already been told their current rules. Records that
     * they've been told, so the next call returns empty until something changes.
     */
    public static Optional<List<String>> takePending(RulesData data, UUID player, String name) {
        RuleOverrides effective = EffectiveRules.effective(data, player);
        String current = effective.canonical();
        String last = data.playerEntry(player).map(PlayerEntry::lastNotified).orElse("");

        if (current.equals(last)) {
            return Optional.empty();
        }

        PlayerEntry entry = data.entry(player);
        entry.setLastNotified(current);
        entry.setLastKnownName(name);
        data.changed();

        return Optional.of(describe(effective));
    }

    /** The lines for a player's current rules; also what the player sees from the "mine" command. */
    public static List<String> describe(RuleOverrides effective) {
        List<String> lines = new ArrayList<>();

        if (effective.isEmpty()) {
            lines.add("Your custom game rules have been removed. The world's rules apply to you.");
            return lines;
        }

        lines.add("Your game rules on this server are different from the world's:");
        effective.entries().forEach((key, value) -> lines.add("  " + shortName(key) + " = " + value));

        return lines;
    }

    /** {@code minecraft:keep_inventory} becomes {@code keep_inventory}. */
    public static String shortName(String key) {
        int colon = key.indexOf(':');

        return colon < 0 ? key : key.substring(colon + 1);
    }
}
