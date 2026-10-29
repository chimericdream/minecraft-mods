package com.chimericdream.forwardingaddress.test.migration;

import com.chimericdream.forwardingaddress.migration.ConversionQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConversionQueueTest {
    private static final String LEVEL = "overworld";

    private final ConversionQueue<String, Integer, String> queue = new ConversionQueue<>();
    private final List<String> converted = new ArrayList<>();

    /**
     * A chunk "n" holds positions "n-0" .. "n-(count-1)".
     */
    private static ConversionQueue.Scanner<String, Integer, String> positionsPerChunk(int count) {
        return (level, chunk) -> {
            List<String> positions = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                positions.add(chunk + "-" + i);
            }
            return positions;
        };
    }

    private int drain(int budget, ConversionQueue.Scanner<String, Integer, String> scanner) {
        return queue.drain(LEVEL, budget, scanner, (level, position) -> converted.add(position));
    }

    @Test
    void convertsNothingWhenNothingIsReported() {
        assertEquals(0, drain(10, positionsPerChunk(3)));
        assertTrue(converted.isEmpty());
    }

    @Test
    void neverConvertsMoreThanTheBudgetInOneDrain() {
        queue.enqueueChunk(LEVEL, 1);

        assertEquals(4, drain(4, positionsPerChunk(10)));
        assertEquals(List.of("1-0", "1-1", "1-2", "1-3"), converted);
        assertEquals(6, queue.pendingPositions(LEVEL));
    }

    @Test
    void leftoverPositionsFinishOnTheNextDrain() {
        queue.enqueueChunk(LEVEL, 1);

        drain(4, positionsPerChunk(6));
        assertEquals(2, drain(4, positionsPerChunk(6)));

        assertEquals(6, converted.size());
        assertEquals(0, queue.pendingPositions(LEVEL));
        assertEquals(0, queue.pendingChunks(LEVEL));
    }

    @Test
    void aBudgetSpansChunks() {
        queue.enqueueChunk(LEVEL, 1);
        queue.enqueueChunk(LEVEL, 2);

        assertEquals(5, drain(5, positionsPerChunk(3)));
        assertEquals(List.of("1-0", "1-1", "1-2", "2-0", "2-1"), converted);
    }

    @Test
    void aChunkReportedTwiceIsScannedOnce() {
        queue.enqueueChunk(LEVEL, 1);
        queue.enqueueChunk(LEVEL, 1);

        assertEquals(3, drain(100, positionsPerChunk(3)));
    }

    @Test
    void aChunkWithNothingToConvertCostsNoBudget() {
        queue.enqueueChunk(LEVEL, 1);
        queue.enqueueChunk(LEVEL, 2);

        int count = drain(1, (level, chunk) -> chunk == 1 ? List.of() : List.of("2-0"));

        assertEquals(1, count);
        assertEquals(List.of("2-0"), converted);
    }

    @Test
    void anUnloadedChunkIsDroppedNotRetried() {
        queue.enqueueChunk(LEVEL, 1);

        assertEquals(0, drain(10, (level, chunk) -> List.of()));
        assertEquals(0, queue.pendingChunks(LEVEL));
        assertEquals(0, drain(10, positionsPerChunk(3)));
    }

    @Test
    void scansPerDrainAreBounded() {
        for (int chunk = 0; chunk < ConversionQueue.MAX_SCANS_PER_DRAIN + 10; chunk++) {
            queue.enqueueChunk(LEVEL, chunk);
        }

        drain(1000, (level, chunk) -> List.of());

        assertEquals(10, queue.pendingChunks(LEVEL));
    }

    @Test
    void levelsAreIndependent() {
        queue.enqueueChunk("nether", 1);

        assertEquals(0, drain(10, positionsPerChunk(3)));
        assertEquals(1, queue.pendingChunks("nether"));
    }

    @Test
    void clearingALevelForgetsItsWork() {
        queue.enqueueChunk(LEVEL, 1);
        drain(1, positionsPerChunk(5));

        queue.clear(LEVEL);

        assertEquals(0, queue.pendingChunks(LEVEL));
        assertEquals(0, queue.pendingPositions(LEVEL));
    }

    @Test
    void aSkippedPositionStillCountsTowardsTheBudget() {
        queue.enqueueChunk(LEVEL, 1);

        // The converter is told about every position, converted or not; the queue can't tell them apart.
        assertEquals(2, drain(2, positionsPerChunk(5)));
    }
}
