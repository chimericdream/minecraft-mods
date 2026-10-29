package com.chimericdream.forwardingaddress.migration;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds what needs converting in a loaded chunk. Sections whose palette holds no migrated block are
 * skipped without looking at a single block.
 */
public final class ChunkScanner {
    private final MigrationRegistry registry;

    public ChunkScanner(MigrationRegistry registry) {
        this.registry = registry;
    }

    /**
     * @return positions of migrated blocks, plus positions of block-entity containers that may hold
     * migrated items; empty if the chunk is no longer loaded
     */
    public List<BlockPos> scan(ServerLevel level, ChunkPos chunkPos) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x(), chunkPos.z());
        if (chunk == null) {
            return List.of();
        }

        Set<BlockPos> found = new LinkedHashSet<>();

        if (registry.hasBlockMigrations()) {
            LevelChunkSection[] sections = chunk.getSections();
            for (int index = 0; index < sections.length; index++) {
                LevelChunkSection section = sections[index];
                if (section.hasOnlyAir() || !section.maybeHas(state -> registry.isMigratedBlock(state.getBlock()))) {
                    continue;
                }

                int baseY = (chunk.getMinSectionY() + index) << 4;
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                            if (registry.isMigratedBlock(section.getBlockState(x, y, z).getBlock())) {
                                found.add(new BlockPos(chunkPos.getMinBlockX() + x, baseY + y, chunkPos.getMinBlockZ() + z));
                            }
                        }
                    }
                }
            }
        }

        if (registry.hasItemMigrations()) {
            for (BlockEntity entity : chunk.getBlockEntities().values()) {
                if (entity instanceof Container) {
                    found.add(entity.getBlockPos().immutable());
                }
            }
        }

        return new ArrayList<>(found);
    }
}
