package com.chimericdream.rulesforthee.rule;

import com.chimericdream.rulesforthee.data.RulesData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collection;

/**
 * Phantom spawning is the one rule vanilla checks once for the whole level before it loops over the players,
 * so a per-player value needs two decisions: should the spawner run at all this tick, and is this particular
 * player a candidate.
 */
public final class PhantomRules {
    private PhantomRules() {
    }

    /**
     * Whether the spawner should run. True if the world allows phantoms, or if any of {@code players} has an
     * override allowing them: the spawner then runs, and {@link #allowedFor} filters out everyone else.
     */
    public static boolean gateOpen(RulesData data, Collection<? extends ServerPlayer> players, boolean worldValue) {
        if (worldValue) {
            return true;
        }

        for (ServerPlayer player : players) {
            if (EffectiveRules.override(data, player.getUUID(), GameRules.SPAWN_PHANTOMS).orElse(false)) {
                return true;
            }
        }

        return false;
    }

    public static boolean gateOpen(ServerLevel level, boolean worldValue) {
        if (worldValue) {
            return true;
        }

        return gateOpen(RulesData.get(level.getServer()), level.players(), false);
    }

    /** Whether phantoms may spawn for this player: their override, or the world's value. */
    public static boolean allowedFor(ServerPlayer player) {
        return PlayerRules.resolve(player, GameRules.SPAWN_PHANTOMS, player.level().getGameRules().get(GameRules.SPAWN_PHANTOMS));
    }
}
