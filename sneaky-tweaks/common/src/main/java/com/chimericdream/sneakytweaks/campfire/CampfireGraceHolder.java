package com.chimericdream.sneakytweaks.campfire;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public interface CampfireGraceHolder {
    // Defined here rather than as a field on the Player mixin: a field initializer merged into
    // Player's own <clinit> triggers a NeoForge 26.2.0.15-beta ExceptionInInitializerError during
    // the "Registry initialization" phase (see git history / crash reports for the investigation).
    // Defining it on this plain interface keeps SynchedEntityData.defineId() out of Player's clinit entirely.
    EntityDataAccessor<Integer> ST$CAMPFIRE_GRACE_TICKS = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    int st$getCampfireGraceTicks();

    void st$setCampfireGraceTicks(int ticks);
}
