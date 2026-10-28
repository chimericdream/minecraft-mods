package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Where the mixins into Minecraft's save-data loading hand over. Spike: counts what it sees so a test
 * can tell which load paths actually reach it. Runs on whichever thread loads the data, which for
 * chunks is a background worker.
 */
public final class SavedDataRewrites {
    public static final AtomicLong CALLS = new AtomicLong();
    public static final AtomicLong RENAMES = new AtomicLong();

    private static final Set<String> SEEN = ConcurrentHashMap.newKeySet();

    private SavedDataRewrites() {
    }

    public static void apply(String source, CompoundTag tag) {
        CALLS.incrementAndGet();
        if (SEEN.add(source)) {
            ForwardingAddressMod.LOGGER.info("[spike] saved-data hook reached from {}", source);
        }

        int renamed = MigrationRegistry.INSTANCE.rewriter().rewrite(tag);
        if (renamed > 0) {
            RENAMES.addAndGet(renamed);
            ForwardingAddressMod.LOGGER.info("[spike] renamed {} retired id(s) in {} data", renamed, source);
        }
    }
}
