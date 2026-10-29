package com.chimericdream.rulesforthee.fabric.test;

import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.PhantomRules;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PhantomSpawner;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Phantom spawning is checked once per level, then per player. The per-player filter inside the vanilla loop
 * isn't observable from here (a spawn also depends on night, sky access, difficulty and a random roll), so
 * these cover the decisions and the level-wide gate; the filter is covered by the manual test plan.
 */
@SuppressWarnings("unused")
public class PhantomRulesGameTest {
    private static ServerPlayer mockPlayer(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    private static void withWorld(MinecraftServer server, boolean value, Runnable body) {
        GameRules rules = server.overworld().getGameRules();
        boolean previous = rules.get(GameRules.SPAWN_PHANTOMS);
        rules.set(GameRules.SPAWN_PHANTOMS, value, server);
        try {
            body.run();
        } finally {
            rules.set(GameRules.SPAWN_PHANTOMS, previous, server);
        }
    }

    private static int nextTick(PhantomSpawner spawner) throws ReflectiveOperationException {
        Field field = PhantomSpawner.class.getDeclaredField("nextTick");
        field.setAccessible(true);

        return field.getInt(spawner);
    }

    @GameTest
    public void aPlayerOverrideDecidesWhetherPhantomsAreAllowedForThem(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer player = mockPlayer(context);
        RulesData data = RulesData.get(server);

        try {
            withWorld(server, true, () -> {
                context.assertTrue(PhantomRules.allowedFor(player), "world on, no override: allowed");
                data.entry(player.getUUID()).overrides().set(GameRules.SPAWN_PHANTOMS, false);
                context.assertTrue(!PhantomRules.allowedFor(player), "world on, override off: not allowed");
            });

            withWorld(server, false, () -> {
                context.assertTrue(!PhantomRules.allowedFor(player), "world off, override off: not allowed");
                data.entry(player.getUUID()).overrides().set(GameRules.SPAWN_PHANTOMS, true);
                context.assertTrue(PhantomRules.allowedFor(player), "world off, override on: allowed");
            });
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void theGateOpensWhenAnyPlayerIsAllowed(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer allowed = mockPlayer(context);
        ServerPlayer other = mockPlayer(context);
        RulesData data = RulesData.get(server);

        try {
            context.assertTrue(PhantomRules.gateOpen(data, List.of(allowed, other), true), "world on: open");
            context.assertTrue(!PhantomRules.gateOpen(data, List.of(allowed, other), false), "world off, no overrides: closed");
            context.assertTrue(!PhantomRules.gateOpen(data, List.of(), false), "world off, nobody online: closed");

            data.entry(allowed.getUUID()).overrides().set(GameRules.SPAWN_PHANTOMS, true);
            context.assertTrue(PhantomRules.gateOpen(data, List.of(allowed, other), false), "world off, one player allowed: open");
            context.assertTrue(!PhantomRules.gateOpen(data, List.of(other), false), "that player isn't online: closed");
            context.succeed();
        } finally {
            data.removePlayer(allowed.getUUID());
            data.removePlayer(other.getUUID());
        }
    }

    @GameTest
    public void theVanillaSpawnerRunsOnlyWhenTheGateIsOpen(GameTestHelper context) throws ReflectiveOperationException {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        PhantomSpawner spawner = new PhantomSpawner();

        // The spawner's timer is reset to a positive number whenever it passes the level-wide rule check.
        withWorld(server, false, () -> {
            try {
                spawner.tick(level, true);
                context.assertTrue(nextTick(spawner) == 0, "world off, nobody allowed: the spawner doesn't run");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        });

        withWorld(server, true, () -> {
            try {
                spawner.tick(level, true);
                context.assertTrue(nextTick(spawner) > 0, "world on: the spawner runs");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        });
        context.succeed();
    }
}
