package com.chimericdream.polycraft.data;

import com.chimericdream.polycraft.ModInfo;
import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.core.ChoiceStore;
import com.chimericdream.polycraft.recipe.PolyCraftEpoch;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Each player's remembered recipe choices, stored with the world. A player's entries are saved as a list so their
 * least-recently-used order survives a restart. Turning {@code rememberChoices} off in the config stops reading and
 * writing choices but leaves the saved ones alone.
 */
public class PolyCraftChoices extends SavedData {
    /** At most this many remembered choices per player; the least recently used goes first. */
    public static final int MAX_PER_PLAYER = 256;

    private record Entry(String group, String recipe) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("group").forGetter(Entry::group),
            Codec.STRING.fieldOf("recipe").forGetter(Entry::recipe)
        ).apply(instance, Entry::new));
    }

    public static final Codec<PolyCraftChoices> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, Entry.CODEC.listOf()).optionalFieldOf("players", Map.of()).forGetter(PolyCraftChoices::toSaved)
    ).apply(instance, PolyCraftChoices::new));

    public static final SavedDataType<PolyCraftChoices> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "choices"),
        PolyCraftChoices::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    private final ChoiceStore store = new ChoiceStore(MAX_PER_PLAYER);

    public PolyCraftChoices() {
    }

    private PolyCraftChoices(Map<UUID, List<Entry>> saved) {
        Map<UUID, Map<String, String>> asMaps = new LinkedHashMap<>();
        saved.forEach((player, entries) -> {
            Map<String, String> own = new LinkedHashMap<>();
            entries.forEach(entry -> own.put(entry.group(), entry.recipe()));
            asMaps.put(player, own);
        });
        store.restore(asMaps);
    }

    public static PolyCraftChoices get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Optional<String> choiceFor(UUID player, String groupKey) {
        if (!PolyCraftConfig.get().rememberChoices) {
            return Optional.empty();
        }

        return store.get(player, groupKey);
    }

    public void choose(UUID player, String groupKey, String recipeId) {
        if (!PolyCraftConfig.get().rememberChoices) {
            return;
        }

        if (store.set(player, groupKey, recipeId)) {
            setDirty();
            PolyCraftEpoch.bump();
        }
    }

    public int reset(UUID player) {
        int removed = store.clear(player);
        if (removed > 0) {
            setDirty();
            PolyCraftEpoch.bump();
        }

        return removed;
    }

    public int resetAll() {
        int removed = store.clearAll();
        if (removed > 0) {
            setDirty();
            PolyCraftEpoch.bump();
        }

        return removed;
    }

    public int prune(Predicate<String> recipeExists) {
        int removed = store.prune(recipeExists);
        if (removed > 0) {
            setDirty();
        }

        return removed;
    }

    private Map<UUID, List<Entry>> toSaved() {
        Map<UUID, List<Entry>> saved = new LinkedHashMap<>();
        store.snapshot().forEach((player, own) -> {
            List<Entry> entries = new ArrayList<>(own.size());
            own.forEach((group, recipe) -> entries.add(new Entry(group, recipe)));
            saved.put(player, entries);
        });
        return saved;
    }
}
