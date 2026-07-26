package com.chimericdream.lib.testkit.gametest;

import java.util.List;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Reusable entity-presence assertions for GameTests, built on
 * {@link GameTestHelper#getEntities(EntityType)} (which is scoped to the test's own region). The most
 * common need is a "no leaked entities" watcher for things that are supposed to clean themselves up —
 * e.g. {@code SimpleSeatEntity}, which auto-despawns once its rider leaves. Published in the shared
 * {@code testFixtures} variant so downstream mods reuse it instead of copying.
 */
public final class GameTestEntities {
    private GameTestEntities() {
    }

    /** Returns every entity of {@code type} currently inside the test region. */
    public static <E extends Entity> List<E> of(GameTestHelper context, EntityType<E> type) {
        return context.getEntities(type);
    }

    /** Counts the entities of {@code type} in the test region. */
    public static int count(GameTestHelper context, EntityType<?> type) {
        return context.getEntities(type).size();
    }

    /** Fails the test unless zero entities of {@code type} remain in the test region. */
    public static void assertNone(GameTestHelper context, EntityType<?> type) {
        int count = count(context, type);

        if (count != 0) {
            context.fail("Expected no " + type + " entities to remain, but found " + count);
        }
    }

    /** Fails the test unless exactly {@code expected} entities of {@code type} are present. */
    public static void assertCount(GameTestHelper context, EntityType<?> type, int expected) {
        int count = count(context, type);

        if (count != expected) {
            context.fail("Expected " + expected + " " + type + " entities, but found " + count);
        }
    }
}
