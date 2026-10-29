package com.chimericdream.rulesforthee.rule;

import com.chimericdream.rulesforthee.data.PlayerEntry;
import com.chimericdream.rulesforthee.data.RulesData;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.Optional;
import java.util.UUID;

/**
 * Works out which value of a game rule applies to a player. The order is the player's own override, then
 * their profile, then the world's value.
 *
 * <p>This holds only the lookup logic, with no Minecraft server types, so it can be unit tested.
 * {@link PlayerRules} is the game-facing wrapper the mixins call.
 */
public final class EffectiveRules {
    private EffectiveRules() {
    }

    /** The player's override of {@code rule}, from themself or their profile, if either has one. */
    public static <T> Optional<T> override(RulesData data, UUID player, GameRule<T> rule) {
        Optional<PlayerEntry> entry = data.playerEntry(player);
        if (entry.isEmpty()) {
            return Optional.empty();
        }

        Optional<T> own = entry.get().overrides().get(rule);
        if (own.isPresent()) {
            return own;
        }

        // Only the first profile counts in v1; see PlayerEntry#profile.
        return entry.get().profile()
            .flatMap(data::profile)
            .flatMap(profile -> profile.get(rule));
    }

    public static <T> T resolve(RulesData data, UUID player, GameRule<T> rule, T worldValue) {
        return override(data, player, rule).orElse(worldValue);
    }

    /** Every rule the player overrides, with their profile's values under their own. */
    public static RuleOverrides effective(RulesData data, UUID player) {
        RuleOverrides merged = new RuleOverrides();

        Optional<PlayerEntry> entry = data.playerEntry(player);
        if (entry.isEmpty()) {
            return merged;
        }

        entry.get().profile().flatMap(data::profile).ifPresent(merged::putAll);
        merged.putAll(entry.get().overrides());

        return merged;
    }
}
