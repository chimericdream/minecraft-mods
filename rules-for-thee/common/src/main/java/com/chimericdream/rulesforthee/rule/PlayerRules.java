package com.chimericdream.rulesforthee.rule;

import com.chimericdream.rulesforthee.data.RulesData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * What the mixins call. Each takes the value vanilla was about to use as {@code worldValue}, so a player
 * with no override gets exactly what vanilla would have given them.
 */
public final class PlayerRules {
    private PlayerRules() {
    }

    /**
     * Resolves {@code rule} for {@code entity} if it's a server-side player, otherwise returns the world
     * value. Mixins that wrap code shared with mobs can pass any entity.
     */
    public static <T> T resolve(Entity entity, GameRule<T> rule, T worldValue) {
        if (!(entity instanceof ServerPlayer player)) {
            return worldValue;
        }

        return resolve(player.level().getServer(), player, rule, worldValue);
    }

    /** As {@link #resolve(Entity, GameRule, Object)}, reading the world value from {@code worldRules}. */
    public static <T> T resolve(Entity entity, GameRules worldRules, GameRule<T> rule) {
        return resolve(entity, rule, worldRules.get(rule));
    }

    /**
     * For mixin handlers, which see a wrapped call's rule and value only as {@code GameRule<?>} and
     * {@code Object}. The value comes from that same rule, so the types always agree.
     */
    @SuppressWarnings("unchecked")
    public static Object resolveUnchecked(Entity entity, GameRule<?> rule, Object worldValue) {
        return resolve(entity, (GameRule<Object>) rule, worldValue);
    }

    public static <T> T resolve(MinecraftServer server, ServerPlayer player, GameRule<T> rule, T worldValue) {
        if (server == null) {
            return worldValue;
        }

        return EffectiveRules.resolve(RulesData.get(server), player.getUUID(), rule, worldValue);
    }
}
