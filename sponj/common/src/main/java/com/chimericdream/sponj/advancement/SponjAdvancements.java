package com.chimericdream.sponj.advancement;

import com.chimericdream.sponj.ModInfo;
import com.chimericdream.sponj.registry.ModStats;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public final class SponjAdvancements {
    public static final Identifier ROOT = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "root");
    public static final Identifier BIG_GULP = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "big_gulp");
    public static final Identifier SPILL_RESPONSE_TEAM = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "spill_response_team");
    public static final Identifier DRY_HEAT = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dry_heat");
    public static final Identifier SPACE_HEATER = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "space_heater");

    public static final int SPILL_RESPONSE_TEAM_THRESHOLD = 100_000;

    /**
     * Sponj absorption and dry-out happen as passive block reactions (neighborChanged/onPlace), which
     * have no player in scope. Since there's no way to know who's actually responsible, credit whoever
     * is standing close enough that they're almost certainly the one who caused it.
     */
    private static final double NEARBY_PLAYER_RADIUS = 16.0;

    private SponjAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        MinecraftServer server = player.level().getServer();
        AdvancementHolder advancement = server.getAdvancements().get(advancementId);

        if (advancement != null) {
            player.getAdvancements().award(advancement, "magic");
        }
    }

    /** Awards {@code advancementId} to every player within {@link #NEARBY_PLAYER_RADIUS} of {@code pos}. */
    public static void awardNearby(Level world, BlockPos pos, Identifier advancementId) {
        forNearbyPlayers(world, pos, player -> award(player, advancementId));
    }

    /**
     * Increments the water-absorbed stat by {@code amount} for every player within
     * {@link #NEARBY_PLAYER_RADIUS} of {@code pos}, then awards Spill Response Team to any of them
     * whose running total has crossed {@link #SPILL_RESPONSE_TEAM_THRESHOLD}.
     */
    public static void incrementWaterAbsorbed(Level world, BlockPos pos, int amount) {
        forNearbyPlayers(world, pos, player -> {
            player.awardStat(ModStats.WATER_ABSORBED, amount);

            if (player.getStats().getValue(Stats.CUSTOM.get(ModStats.WATER_ABSORBED)) >= SPILL_RESPONSE_TEAM_THRESHOLD) {
                award(player, SPILL_RESPONSE_TEAM);
            }
        });
    }

    private static void forNearbyPlayers(Level world, BlockPos pos, Consumer<ServerPlayer> action) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        double radiusSq = NEARBY_PLAYER_RADIUS * NEARBY_PLAYER_RADIUS;

        for (ServerPlayer player : serverLevel.players()) {
            if (player.blockPosition().distSqr(pos) <= radiusSq) {
                action.accept(player);
            }
        }
    }
}
