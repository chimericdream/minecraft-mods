package com.chimericdream.bettertargetdummies.entity;

import com.chimericdream.bettertargetdummies.ModInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

public final class TargetDummyMarker {
    public static final String TAG = ModInfo.MOD_ID + ":dummy";

    private TargetDummyMarker() {
    }

    public static void mark(Entity entity) {
        entity.addTag(TAG);
    }

    public static boolean isDummy(Entity entity) {
        return entity.entityTags().contains(TAG);
    }

    public static void reportDamage(LivingEntity dummy, DamageSource source, float damage) {
        if (damage <= 0.0F || !(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Component message = Component.literal(String.format(Locale.ROOT, "%.1f damage → ", damage))
            .append(dummy.getDisplayName());

        player.sendOverlayMessage(message);
    }

    /** Whether {@code source} is attributable to a player swinging/shooting at the dummy on purpose. */
    public static boolean isFromPlayer(DamageSource source) {
        return source.getEntity() instanceof ServerPlayer;
    }
}
