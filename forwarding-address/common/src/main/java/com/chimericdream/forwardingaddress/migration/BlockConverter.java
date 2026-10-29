package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.PropertyMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Replaces one placed block with its migrated counterpart without losing block-entity data.
 *
 * <p>{@code setBlock} never keeps the block entity, even between blocks that share a block-entity type,
 * and removing the old block drops its container slots. So the contents are snapshotted and cleared
 * first, and put back afterwards. See {@code docs/BLOCK-MIGRATION.md}.
 */
public final class BlockConverter {
    public enum Result {
        /**
         * The block was replaced.
         */
        CONVERTED,
        /**
         * A migration applies but can't be done exactly, so the block was left alone and the reason
         * logged. It is tried again the next time its chunk loads.
         */
        SKIPPED,
        /**
         * No migration applies to the block at this position.
         */
        NOT_MIGRATED
    }

    private final MigrationRegistry registry;
    private final MigrationLog log;

    public BlockConverter(MigrationRegistry registry, MigrationLog log) {
        this.registry = registry;
        this.log = log;
    }

    public Result convert(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        MigrationRegistry.ResolvedBlock resolved = registry.forBlock(state.getBlock());
        if (resolved == null) {
            return Result.NOT_MIGRATED;
        }

        BlockMigration migration = resolved.migration();
        BlockState newState = PropertyCopier.copy(state, resolved.to().defaultBlockState());

        PropertyMapper propertyMapper = migration.propertyMapper();
        if (propertyMapper != null) {
            try {
                newState = propertyMapper.map(state, newState);
            } catch (RuntimeException e) {
                log.logger().error("The property mapper for {} failed at {}; leaving the block alone", migration.from(), pos, e);
                return Result.SKIPPED;
            }
        }

        BlockEntity oldEntity = level.getBlockEntity(pos);
        if (oldEntity == null) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            return Result.CONVERTED;
        }

        return convertWithBlockEntity(level, pos, migration, newState, oldEntity);
    }

    private Result convertWithBlockEntity(ServerLevel level, BlockPos pos, BlockMigration migration, BlockState newState, BlockEntity oldEntity) {
        if (!newState.hasBlockEntity()) {
            log.logger().error("Cannot convert {} at {} to {}: the new block has no block entity, so its data would be lost", migration.from(), pos, migration.to());
            return Result.SKIPPED;
        }

        boolean sameType = oldEntity.getType().isValid(newState);
        if (!sameType && migration.entityMapper() == null) {
            log.logger().error("Cannot convert {} at {} to {}: they use different block entity types and the migration has no block entity mapper", migration.from(), pos, migration.to());
            return Result.SKIPPED;
        }

        if (!sameType && !(newState.getBlock() instanceof EntityBlock entityBlock && entityBlock.newBlockEntity(pos, newState) != null)) {
            log.logger().error("Cannot convert {} at {} to {}: the new block did not create a block entity", migration.from(), pos, migration.to());
            return Result.SKIPPED;
        }

        int hidden = migration.hiddenSlots() == null ? 0 : migration.hiddenSlots().applyAsInt(oldEntity);
        List<ItemStack> saved = snapshot(oldEntity, hidden);
        BlockState oldState = level.getBlockState(pos);
        CompoundTag oldData = sameType ? null : oldEntity.saveWithFullMetadata(level.registryAccess());

        // Empty the container first so removing the old block has nothing to drop.
        clear(oldEntity, saved.size());
        level.setBlock(pos, newState, Block.UPDATE_ALL);

        if (sameType) {
            oldEntity.clearRemoved();
            oldEntity.setBlockState(newState);
            level.setBlockEntity(oldEntity);
            restore(oldEntity, saved);
            return Result.CONVERTED;
        }

        BlockEntity newEntity = level.getBlockEntity(pos);
        try {
            if (newEntity == null) {
                throw new IllegalStateException("no block entity was created");
            }

            migration.entityMapper().map(oldData, newEntity, level.registryAccess());
            newEntity.setChanged();
            return Result.CONVERTED;
        } catch (RuntimeException e) {
            log.logger().error("The block entity mapper for {} failed at {}; putting the original block back", migration.from(), pos, e);
            level.setBlock(pos, oldState, Block.UPDATE_ALL);
            oldEntity.clearRemoved();
            oldEntity.setBlockState(oldState);
            level.setBlockEntity(oldEntity);
            restore(oldEntity, saved);
            return Result.SKIPPED;
        }
    }

    private static List<ItemStack> snapshot(BlockEntity entity, int hiddenSlots) {
        List<ItemStack> saved = new ArrayList<>();
        if (entity instanceof Container container) {
            int slots = container.getContainerSize() + hiddenSlots;
            for (int slot = 0; slot < slots; slot++) {
                saved.add(container.getItem(slot).copy());
            }
        }

        return saved;
    }

    private static void clear(BlockEntity entity, int slots) {
        if (entity instanceof Container container) {
            for (int slot = 0; slot < slots; slot++) {
                container.setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    private static void restore(BlockEntity entity, List<ItemStack> saved) {
        if (entity instanceof Container container) {
            for (int slot = 0; slot < saved.size(); slot++) {
                container.setItem(slot, saved.get(slot));
            }
        }
    }
}
