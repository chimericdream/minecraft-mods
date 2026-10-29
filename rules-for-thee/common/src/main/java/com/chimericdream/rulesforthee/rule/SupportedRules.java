package com.chimericdream.rulesforthee.rule;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The game rules that can be overridden per player. A rule is only listed here once something actually
 * applies its override, so the commands never offer a rule that does nothing.
 */
public final class SupportedRules {
    // Keyed by the short name used in commands, e.g. "keep_inventory". Insertion order is the listing order.
    private static final Map<String, GameRule<?>> BY_NAME = new LinkedHashMap<>();

    static {
        register(GameRules.KEEP_INVENTORY);
        register(GameRules.DROWNING_DAMAGE);
        register(GameRules.FALL_DAMAGE);
        register(GameRules.FIRE_DAMAGE);
        register(GameRules.FREEZE_DAMAGE);
        register(GameRules.SHOW_DEATH_MESSAGES);
        register(GameRules.SHOW_ADVANCEMENT_MESSAGES);
    }

    private SupportedRules() {
    }

    private static void register(GameRule<?> rule) {
        BY_NAME.put(nameOf(rule), rule);
    }

    /** The name a rule goes by in commands: its identifier without the namespace. */
    public static String nameOf(GameRule<?> rule) {
        return rule.getIdentifier().getPath();
    }

    public static List<GameRule<?>> all() {
        return List.copyOf(BY_NAME.values());
    }

    public static Map<String, GameRule<?>> byName() {
        return Collections.unmodifiableMap(BY_NAME);
    }

    public static Optional<GameRule<?>> find(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }

    public static boolean isSupported(GameRule<?> rule) {
        return BY_NAME.get(nameOf(rule)) == rule;
    }
}
