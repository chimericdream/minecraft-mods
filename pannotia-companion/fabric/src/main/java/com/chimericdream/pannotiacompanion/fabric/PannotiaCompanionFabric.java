package com.chimericdream.pannotiacompanion.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.pannotiacompanion.PannotiaCompanionMod;
import com.chimericdream.pannotiacompanion.home.BackData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import com.chimericdream.pannotiacompanion.fabric.loot.PannotiaFabricLoot;

public final class PannotiaCompanionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PannotiaCompanionMod.init();
        PannotiaFabricLoot.init();

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer player) {
                BackData.recordDeath(player);
            }
        });
    }
}
