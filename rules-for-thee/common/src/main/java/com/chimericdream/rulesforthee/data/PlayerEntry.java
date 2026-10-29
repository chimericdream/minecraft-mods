package com.chimericdream.rulesforthee.data;

import com.chimericdream.rulesforthee.rule.RuleOverrides;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Everything the mod stores about one player. */
public final class PlayerEntry {
    public static final Codec<PlayerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("profiles", List.of()).forGetter(entry -> entry.profiles),
        RuleOverrides.CODEC.optionalFieldOf("overrides", new RuleOverrides()).forGetter(entry -> entry.overrides),
        Codec.STRING.optionalFieldOf("name", "").forGetter(entry -> entry.lastKnownName),
        Codec.STRING.optionalFieldOf("last_notified", "").forGetter(entry -> entry.lastNotified)
    ).apply(instance, PlayerEntry::new));

    /**
     * Stored as a list so several profiles can be supported later without a data migration, but v1 allows
     * at most one. {@link #setProfile} is the only place that enforces that.
     */
    private final List<String> profiles;
    private final RuleOverrides overrides;
    private String lastKnownName;
    /** The canonical effective rules this player was last told about. */
    private String lastNotified;

    public PlayerEntry() {
        this(List.of(), new RuleOverrides(), "", "");
    }

    private PlayerEntry(List<String> profiles, RuleOverrides overrides, String lastKnownName, String lastNotified) {
        this.profiles = new ArrayList<>(profiles);
        this.overrides = overrides.copy();
        this.lastKnownName = lastKnownName;
        this.lastNotified = lastNotified;
    }

    /** The player's profile, if any. With the v1 limit this is the only one. */
    public Optional<String> profile() {
        return profiles.isEmpty() ? Optional.empty() : Optional.of(profiles.getFirst());
    }

    /** Replaces the player's profile; an empty value removes it. */
    public void setProfile(Optional<String> profile) {
        profiles.clear();
        profile.ifPresent(profiles::add);
    }

    public List<String> profiles() {
        return List.copyOf(profiles);
    }

    public RuleOverrides overrides() {
        return overrides;
    }

    public String lastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String name) {
        this.lastKnownName = name;
    }

    public String lastNotified() {
        return lastNotified;
    }

    public void setLastNotified(String canonical) {
        this.lastNotified = canonical;
    }

    /** Whether this entry holds nothing worth saving. */
    public boolean isEmpty() {
        return profiles.isEmpty() && overrides.isEmpty() && lastNotified.isEmpty();
    }
}
