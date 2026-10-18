package com.chimericdream.pannotiacompanion.fabric.test;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.pannotiacompanion.home.BackPoint;
import com.chimericdream.pannotiacompanion.home.Cooldowns;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackPointTest extends BootstrapMinecraft {
    private static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:the_nether"));

    @Test
    void codecRoundTripKeepsDimensionPositionAndRotation() {
        BackPoint point = new BackPoint(NETHER, 1.5, 64, -3.25, 90f, 10f);

        var json = BackPoint.CODEC.encodeStart(JsonOps.INSTANCE, point).getOrThrow();
        BackPoint decoded = BackPoint.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();

        assertEquals(point, decoded);
    }

    @Test
    void backCooldownCountsDownLikeTheOtherTeleports() {
        Cooldowns cooldowns = new Cooldowns();
        UUID player = UUID.randomUUID();

        cooldowns.start(player, 100, 5);

        assertEquals(5, cooldowns.remainingSeconds(player, 100));
        assertEquals(0, cooldowns.remainingSeconds(player, 200));
    }
}
