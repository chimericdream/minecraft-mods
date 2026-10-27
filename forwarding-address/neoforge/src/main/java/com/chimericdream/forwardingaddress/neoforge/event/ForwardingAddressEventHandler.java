package com.chimericdream.forwardingaddress.neoforge.event;

import com.chimericdream.forwardingaddress.ModInfo;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class ForwardingAddressEventHandler {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            MigrationEvents.onChunkLoaded(level, event.getChunk().getPos());
        }
    }
}
