package com.chimericdream.forwardingaddress.fabric;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import com.chimericdream.forwardingaddress.migration.RetiredItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

public final class ForwardingAddressFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForwardingAddressMod.init();

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, newlyGenerated) -> MigrationEvents.onChunkLoaded(level, chunk.getPos()));

        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, output) -> {
            output.getDisplayStacks().removeIf(RetiredItems::isRetired);
            output.getSearchTabStacks().removeIf(RetiredItems::isRetired);
        });
    }
}
