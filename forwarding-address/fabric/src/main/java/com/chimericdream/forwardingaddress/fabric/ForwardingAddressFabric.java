package com.chimericdream.forwardingaddress.fabric;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public final class ForwardingAddressFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForwardingAddressMod.init();

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, newlyGenerated) -> MigrationEvents.onChunkLoaded(level, chunk.getPos()));
    }
}
