package com.chimericdream.rulesforthee.data;

import com.chimericdream.rulesforthee.ModInfo;
import com.chimericdream.rulesforthee.rule.RuleOverrides;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

/** Every profile and every player's overrides, stored with the world. */
public class RulesData extends SavedData {
    /** Lowercase so profile names are easy to type and compare. */
    private static final Pattern PROFILE_NAME = Pattern.compile("[a-z0-9_-]{1,32}");

    public static final Codec<RulesData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, RuleOverrides.CODEC).optionalFieldOf("profiles", Map.of()).forGetter(data -> data.profiles),
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerEntry.CODEC).optionalFieldOf("players", Map.of()).forGetter(data -> data.players)
    ).apply(instance, RulesData::new));

    public static final SavedDataType<RulesData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "rules"),
        RulesData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    // Sorted so listings are stable.
    private final Map<String, RuleOverrides> profiles = new TreeMap<>();
    private final Map<UUID, PlayerEntry> players = new HashMap<>();

    public RulesData() {
    }

    private RulesData(Map<String, RuleOverrides> profiles, Map<UUID, PlayerEntry> players) {
        this.profiles.putAll(profiles);
        this.players.putAll(players);
    }

    public static RulesData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public static boolean isValidProfileName(String name) {
        return PROFILE_NAME.matcher(name).matches();
    }

    public Set<String> profileNames() {
        return Collections.unmodifiableSet(profiles.keySet());
    }

    public Optional<RuleOverrides> profile(String name) {
        return Optional.ofNullable(profiles.get(name));
    }

    /** @return false if the name is invalid or a profile with that name already exists */
    public boolean createProfile(String name) {
        if (!isValidProfileName(name) || profiles.containsKey(name)) {
            return false;
        }

        profiles.put(name, new RuleOverrides());
        setDirty();

        return true;
    }

    /** Removes the profile and takes it off every player who had it. */
    public boolean deleteProfile(String name) {
        if (profiles.remove(name) == null) {
            return false;
        }

        players.values().forEach(entry -> {
            if (entry.profile().filter(name::equals).isPresent()) {
                entry.setProfile(Optional.empty());
            }
        });
        setDirty();

        return true;
    }

    public Optional<PlayerEntry> playerEntry(UUID player) {
        return Optional.ofNullable(players.get(player));
    }

    /** Gets the player's entry, creating an empty one if they have none. */
    public PlayerEntry entry(UUID player) {
        return players.computeIfAbsent(player, id -> new PlayerEntry());
    }

    /** Every player the mod holds anything for, mostly for the phantom spawner's "does anyone override this" check. */
    public Map<UUID, PlayerEntry> players() {
        return Collections.unmodifiableMap(players);
    }

    /** Call after changing a profile or a {@link PlayerEntry} obtained from this object. */
    public void changed() {
        setDirty();
    }
}
