package com.chimericdream.rulesforthee.rule;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * A set of game rule values, keyed by rule. Used both for a profile's rules and for a single player's own
 * overrides.
 *
 * <p>Values are stored as the strings vanilla's own {@link GameRule#serialize} produces, so booleans and
 * integers (and any future rule type) share one code path and the saved data doesn't depend on a value
 * type. A key that is no longer a known rule, or a value that no longer parses, is ignored on read rather
 * than failing the whole save.
 */
public final class RuleOverrides {
    public static final Codec<RuleOverrides> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
        .xmap(RuleOverrides::new, RuleOverrides::entries);

    // Sorted so `canonical()` is stable no matter the order rules were set in.
    private final Map<String, String> values = new TreeMap<>();

    public RuleOverrides() {
    }

    private RuleOverrides(Map<String, String> values) {
        this.values.putAll(values);
    }

    public static String keyOf(GameRule<?> rule) {
        return rule.getIdentifier().toString();
    }

    public <T> Optional<T> get(GameRule<T> rule) {
        String raw = values.get(keyOf(rule));

        return raw == null ? Optional.empty() : rule.deserialize(raw).result();
    }

    public <T> void set(GameRule<T> rule, T value) {
        values.put(keyOf(rule), rule.serialize(value));
    }

    /** @return whether the rule had a value to remove */
    public boolean clear(GameRule<?> rule) {
        return values.remove(keyOf(rule)) != null;
    }

    public boolean has(GameRule<?> rule) {
        return values.containsKey(keyOf(rule));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(values.keySet());
    }

    /** Copies every value from {@code other} over this set's values. */
    public void putAll(RuleOverrides other) {
        values.putAll(other.values);
    }

    public RuleOverrides copy() {
        return new RuleOverrides(values);
    }

    /** A stable text form, used to tell whether a player's effective rules changed. */
    public String canonical() {
        StringBuilder out = new StringBuilder();
        values.forEach((key, value) -> out.append(key).append('=').append(value).append(';'));

        return out.toString();
    }

    public Map<String, String> entries() {
        return Collections.unmodifiableMap(values);
    }
}
