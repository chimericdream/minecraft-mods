package com.chimericdream.betterportallinking.portal;

import com.chimericdream.betterportallinking.BetterPortalLinkingMod;
import com.chimericdream.betterportallinking.config.BetterPortalLinkingConfig;
import com.chimericdream.betterportallinking.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Level-facing façade for portal address linking: candidate gathering, grouping, scoring, and
 * selection. The scoring/tiebreak core ({@link #select}) is a separate, level-independent static
 * method so it is unit-testable without a running server.
 */
public final class PortalAddressLinker {
    private static final Predicate<Block> IS_ADDRESS_BLOCK =
        block -> BuiltInRegistries.BLOCK.wrapAsHolder(block).is(ModTags.PORTAL_ADDRESS_BLOCKS);

    private PortalAddressLinker() {
    }

    /**
     * One physical portal's linking-relevant data: the portal-block position vanilla itself would
     * have returned as the "found" position for this portal, and that portal's address.
     */
    public record Candidate(BlockPos representativePos, PortalAddress address) {
    }

    /**
     * Pure scoring/tiebreak core — no {@code Level} access, deterministic, unit-testable.
     *
     * <p>Selection rule: score every candidate against {@code entry}; if the best score is 0,
     * return {@link Optional#empty()} (fall back to vanilla — there is nothing to override). Ties on
     * score are broken by lowest {@code representativePos.distSqr(target)}; ties on that too are
     * broken by lowest {@code representativePos.asLong()}. No randomness anywhere.
     */
    public static Optional<BlockPos> select(List<Candidate> candidates, PortalAddress entry, BlockPos target) {
        if (entry.isEmpty() || candidates.isEmpty()) {
            return Optional.empty();
        }

        Candidate best = null;
        int bestScore = 0;
        double bestDistSqr = Double.MAX_VALUE;
        long bestPacked = Long.MAX_VALUE;

        for (Candidate candidate : candidates) {
            int score = entry.score(candidate.address());
            if (score == 0) {
                continue;
            }

            double distSqr = candidate.representativePos().distSqr(target);
            long packed = candidate.representativePos().asLong();

            boolean better = best == null
                || score > bestScore
                || (score == bestScore && distSqr < bestDistSqr)
                || (score == bestScore && distSqr == bestDistSqr && packed < bestPacked);

            if (better) {
                best = candidate;
                bestScore = score;
                bestDistSqr = distSqr;
                bestPacked = packed;
            }
        }

        return best == null ? Optional.empty() : Optional.of(best.representativePos());
    }

    /**
     * Level-facing entry point, called from the {@code PortalForcer#findClosestPortalPosition}
     * hook. Gathers candidates using exactly vanilla's radius/predicate/filters, groups POI
     * positions into distinct portals keyed on {@code rect.minCorner}, scores each portal's address
     * against {@code entry}, and returns the winner — or {@link Optional#empty()} if address linking
     * is disabled, {@code entry} is empty, or every candidate scores 0 (all of which mean "let
     * vanilla decide").
     */
    public static Optional<BlockPos> findBestMatch(
        ServerLevel level, BlockPos target, boolean toNether, WorldBorder border, PortalAddress entry
    ) {
        if (entry.isEmpty() || !BetterPortalLinkingConfig.HANDLER.instance().enableAddressLinking) {
            return Optional.empty();
        }

        // Exactly vanilla's PortalForcer#findClosestPortalPosition candidate gathering: same
        // radius, same POI predicate, same world-border and stale-POI filters.
        PoiManager poiManager = level.getPoiManager();
        int radius = toNether ? 16 : 128;
        poiManager.ensureLoadedAndValid(level, target, radius);

        List<BlockPos> poiPositions = poiManager.getInSquare(type -> type.is(PoiTypes.NETHER_PORTAL), target, radius, PoiManager.Occupancy.ANY)
            .map(PoiRecord::getPos)
            .filter(border::isWithinBounds)
            .filter(pos -> level.getBlockState(pos).hasProperty(NetherPortalBlock.AXIS))
            .toList();

        if (poiPositions.isEmpty()) {
            if (BetterPortalLinkingConfig.HANDLER.instance().logLinkingDecisions) {
                BetterPortalLinkingMod.LOGGER.info(
                    "Entry address {} found no portals within {} blocks of {}; using vanilla portal linking.",
                    entry, radius, target
                );
            }
            return Optional.empty();
        }

        // The POI index stores one record per portal block, so a 2x3 portal yields 6 positions.
        // Group them into distinct portals, computing each portal's rectangle exactly once.
        List<PortalFrame> resolvedFrames = new ArrayList<>();
        List<Candidate> candidates = new ArrayList<>();

        for (BlockPos pos : poiPositions) {
            if (isAlreadyCovered(resolvedFrames, pos)) {
                continue;
            }

            BlockState portalState = level.getBlockState(pos);
            Direction.Axis axis = portalState.getValue(NetherPortalBlock.AXIS);
            PortalFrame frame = PortalFrame.resolve(p -> level.getBlockState(p) == portalState, pos, axis);
            resolvedFrames.add(frame);

            // The representative position for this portal is whichever of its own POI records
            // vanilla's min-by-distSqr-then-Y comparator would itself have picked.
            BlockPos representative = poiPositions.stream()
                .filter(frame::containsInterior)
                .min(Comparator.<BlockPos>comparingDouble(p -> p.distSqr(target)).thenComparingInt(BlockPos::getY))
                .orElse(pos);

            candidates.add(new Candidate(representative, addressFromFrame(level, frame)));
        }

        Optional<BlockPos> result = select(candidates, entry, target);

        if (BetterPortalLinkingConfig.HANDLER.instance().logLinkingDecisions) {
            logDecision(entry, candidates, result);
        }

        return result;
    }

    /**
     * Reads the entry portal's address for {@code portalBlockPos} in {@code level}. Returns
     * {@link PortalAddress#empty()} if the position is not currently a portal block.
     */
    public static PortalAddress addressOf(Level level, BlockPos portalBlockPos) {
        BlockState state = level.getBlockState(portalBlockPos);
        Optional<Direction.Axis> axis = state.getOptionalValue(NetherPortalBlock.AXIS);
        if (axis.isEmpty()) {
            return PortalAddress.empty();
        }

        PortalFrame frame = PortalFrame.resolve(pos -> level.getBlockState(pos) == state, portalBlockPos, axis.get());
        return addressFromFrame(level, frame);
    }

    private static PortalAddress addressFromFrame(Level level, PortalFrame frame) {
        Function<BlockPos, Block> blockAt = pos -> level.getBlockState(pos).getBlock();
        return PortalAddress.fromCorners(frame.corners(), blockAt, IS_ADDRESS_BLOCK);
    }

    private static boolean isAlreadyCovered(List<PortalFrame> resolvedFrames, BlockPos pos) {
        for (PortalFrame frame : resolvedFrames) {
            if (frame.containsInterior(pos)) {
                return true;
            }
        }
        return false;
    }

    private static void logDecision(PortalAddress entry, List<Candidate> candidates, Optional<BlockPos> result) {
        BetterPortalLinkingMod.LOGGER.info("[BetterPortalLinking] entry address: {}", entry);
        for (Candidate candidate : candidates) {
            BetterPortalLinkingMod.LOGGER.info(
                "[BetterPortalLinking] candidate {} address {} score {}",
                candidate.representativePos(), candidate.address(), entry.score(candidate.address())
            );
        }
        BetterPortalLinkingMod.LOGGER.info(
            "[BetterPortalLinking] winner: {}", result.<Object>map(Object::toString).orElse("none (fall back to vanilla)")
        );
    }
}
