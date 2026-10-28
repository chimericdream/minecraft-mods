package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Where the mixins into Minecraft's save-data loading hand over. Renames retired IDs in the saved data
 * before the game decodes it, so a block or item whose old ID is no longer registered is not lost. Runs
 * on whichever thread loads the data, which for chunks is a background worker.
 */
public final class SavedDataRewrites {
    /** How many times the hook has run. Tests use it to tell "nothing to rename" from "never reached". */
    public static final AtomicLong CALLS = new AtomicLong();
    /** How many IDs have been renamed. */
    public static final AtomicLong RENAMES = new AtomicLong();

    private SavedDataRewrites() {
    }

    public static void apply(String source, CompoundTag tag) {
        CALLS.incrementAndGet();

        int renamed = MigrationRegistry.INSTANCE.rewriter().rewrite(tag);
        if (renamed > 0) {
            RENAMES.addAndGet(renamed);
            ForwardingAddressMod.LOGGER.info("Renamed {} retired ID(s) in {} data", renamed, source);
        }
    }
}
