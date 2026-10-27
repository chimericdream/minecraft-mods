package com.chimericdream.forwardingaddress.migration;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Spreads conversions over server ticks. Chunk load only {@link #enqueueChunk reports} the chunk; a
 * per-tick {@link #drain} scans reported chunks and converts at most a fixed number of positions, so a
 * chunk full of old blocks can't spike the tick. Nothing here is persisted: a chunk that unloads
 * mid-drain is simply dropped by the scanner and rescanned on its next load.
 *
 * <p>Generic over the level key {@code L}, chunk key {@code C}, and position {@code P} so the budget
 * logic can be tested without Minecraft.
 */
public final class ConversionQueue<L, C, P> {
    /**
     * Most chunks scanned in one drain. Scanning only reads section palettes, so this is cheap, but it
     * still bounds a burst of chunk loads (for instance on login).
     */
    public static final int MAX_SCANS_PER_DRAIN = 32;

    /**
     * Finds the positions in a chunk that need conversion; an empty list for a chunk with none or one
     * that is no longer loaded.
     */
    @FunctionalInterface
    public interface Scanner<L, C, P> {
        List<P> scan(L level, C chunk);
    }

    @FunctionalInterface
    public interface Converter<L, P> {
        void convert(L level, P position);
    }

    // Chunk-load events may arrive off the server thread, so reports go through a concurrent queue and
    // are moved into the (server-thread-only) state below by drain().
    private final Map<L, Queue<C>> reported = new ConcurrentHashMap<>();
    private final Map<L, Set<C>> chunks = new ConcurrentHashMap<>();
    private final Map<L, Set<P>> positions = new ConcurrentHashMap<>();

    public void enqueueChunk(L level, C chunk) {
        reported.computeIfAbsent(level, key -> new ConcurrentLinkedQueue<>()).add(chunk);
    }

    /**
     * Scans and converts for one level, on the server thread.
     *
     * @param budget the most positions to convert; every position counts as 1 whether it converts or is
     *               skipped
     * @return how many positions were handed to the converter
     */
    public int drain(L level, int budget, Scanner<L, C, P> scanner, Converter<L, P> converter) {
        Queue<C> incoming = reported.get(level);
        Set<C> pendingChunks = chunks.computeIfAbsent(level, key -> new LinkedHashSet<>());
        if (incoming != null) {
            C chunk;
            while ((chunk = incoming.poll()) != null) {
                pendingChunks.add(chunk);
            }
        }

        Set<P> pendingPositions = positions.computeIfAbsent(level, key -> new LinkedHashSet<>());
        int converted = 0;
        int scans = 0;

        while (converted < budget) {
            Iterator<P> iterator = pendingPositions.iterator();
            if (iterator.hasNext()) {
                P next = iterator.next();
                iterator.remove();
                converter.convert(level, next);
                converted++;
                continue;
            }

            if (pendingChunks.isEmpty() || scans >= MAX_SCANS_PER_DRAIN) {
                break;
            }

            C chunk = pendingChunks.iterator().next();
            pendingChunks.remove(chunk);
            scans++;

            pendingPositions.addAll(scanner.scan(level, chunk));
        }

        return converted;
    }

    /**
     * Forgets everything queued for a level, for when it unloads.
     */
    public void clear(L level) {
        reported.remove(level);
        chunks.remove(level);
        positions.remove(level);
    }

    public void clear() {
        reported.clear();
        chunks.clear();
        positions.clear();
    }

    public int pendingChunks(L level) {
        Queue<C> incoming = reported.get(level);
        Set<C> pending = chunks.get(level);
        return (incoming == null ? 0 : incoming.size()) + (pending == null ? 0 : pending.size());
    }

    public int pendingPositions(L level) {
        Set<P> pending = positions.get(level);
        return pending == null ? 0 : pending.size();
    }
}
