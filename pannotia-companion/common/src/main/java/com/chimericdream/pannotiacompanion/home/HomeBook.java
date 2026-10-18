package com.chimericdream.pannotiacompanion.home;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One player's homes. Names are unique per player (case-insensitive); the home limit applies per
 * dimension. Pure logic with no world access, so it is unit-testable.
 */
public class HomeBook {
    public static final String DEFAULT_NAME = "home";
    public static final int MAX_NAME_LENGTH = 32;
    public static final Codec<HomeBook> CODEC = Home.CODEC.listOf().xmap(HomeBook::new, HomeBook::homes);

    public enum SetResult {ADDED, REPLACED, LIMIT_REACHED}

    public enum RenameResult {RENAMED, NOT_FOUND, NAME_TAKEN}

    private final List<Home> homes;

    public HomeBook() {
        this.homes = new ArrayList<>();
    }

    public HomeBook(List<Home> homes) {
        this.homes = new ArrayList<>(homes);
    }

    public List<Home> homes() {
        return List.copyOf(homes);
    }

    public boolean isEmpty() {
        return homes.isEmpty();
    }

    public static boolean isValidName(String name) {
        return !name.isBlank() && name.length() <= MAX_NAME_LENGTH;
    }

    public int countIn(ResourceKey<Level> dimension) {
        return (int) homes.stream().filter(h -> h.dimension().equals(dimension)).count();
    }

    /** Saves {@code home}, overwriting any existing home of the same name. */
    public SetResult set(Home home, int limitPerDimension) {
        int existing = indexOfName(home.name());

        if (existing >= 0) {
            Home old = homes.get(existing);
            // moving a home into a different dimension must respect that dimension's limit
            if (!old.dimension().equals(home.dimension()) && countIn(home.dimension()) >= limitPerDimension) {
                return SetResult.LIMIT_REACHED;
            }

            homes.set(existing, home);
            return SetResult.REPLACED;
        }

        if (countIn(home.dimension()) >= limitPerDimension) {
            return SetResult.LIMIT_REACHED;
        }

        homes.add(home);
        return SetResult.ADDED;
    }

    /** A home by name, or by 1-based position in {@link #homes()} when the query is a number. */
    public Optional<Home> resolve(String query) {
        int byName = indexOfName(query);
        if (byName >= 0) {
            return Optional.of(homes.get(byName));
        }

        try {
            int id = Integer.parseInt(query.trim());
            if (id >= 1 && id <= homes.size()) {
                return Optional.of(homes.get(id - 1));
            }
        } catch (NumberFormatException ignored) {
        }

        return Optional.empty();
    }

    /** The home called "home", else the only home, else nothing. */
    public Optional<Home> resolveDefault() {
        int byName = indexOfName(DEFAULT_NAME);
        if (byName >= 0) {
            return Optional.of(homes.get(byName));
        }

        return homes.size() == 1 ? Optional.of(homes.getFirst()) : Optional.empty();
    }

    public Optional<Home> remove(String query) {
        Optional<Home> found = resolve(query);
        found.ifPresent(homes::remove);
        return found;
    }

    public RenameResult rename(String query, String newName) {
        Optional<Home> found = resolve(query);
        if (found.isEmpty()) {
            return RenameResult.NOT_FOUND;
        }

        int index = homes.indexOf(found.get());
        int taken = indexOfName(newName);
        if (taken >= 0 && taken != index) {
            return RenameResult.NAME_TAKEN;
        }

        homes.set(index, found.get().withName(newName));
        return RenameResult.RENAMED;
    }

    private int indexOfName(String name) {
        for (int i = 0; i < homes.size(); i++) {
            if (homes.get(i).name().equalsIgnoreCase(name.trim())) {
                return i;
            }
        }

        return -1;
    }
}
