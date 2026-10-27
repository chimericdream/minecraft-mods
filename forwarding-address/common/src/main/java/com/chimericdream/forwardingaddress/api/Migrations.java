package com.chimericdream.forwardingaddress.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Entry point for declaring migrations from code. Declare them while your mod initializes, and always
 * finish a chain with {@code register()}:
 *
 * <pre>{@code
 * Migrations.block(Identifier.fromNamespaceAndPath("oldmod", "oak_trim"))
 *     .to(Identifier.fromNamespaceAndPath("newmod", "oak_trim"))
 *     .register();
 * }</pre>
 *
 * <p>The old block (or item) has to stay registered, so worlds and inventories can still load it. A
 * block migration also converts the block's item. A migration whose target isn't installed is skipped
 * and logged, so declaring one never makes your mod depend on the target's mod.
 */
public final class Migrations {
    /**
     * Bumped when a change to this package breaks existing callers.
     */
    public static final int API_VERSION = 1;

    @Nullable
    private static volatile MigrationBackend backend;

    private Migrations() {
    }

    @ApiStatus.Internal
    public static void installBackend(MigrationBackend newBackend) {
        backend = Objects.requireNonNull(newBackend);
    }

    public static BlockBuilder block(Identifier from) {
        return new BlockBuilder(from);
    }

    public static ItemBuilder item(Identifier from) {
        return new ItemBuilder(from);
    }

    private static MigrationBackend requireBackend() {
        MigrationBackend current = backend;
        if (current == null) {
            throw new IllegalStateException("Forwarding Address is not initialized yet; declare migrations after your mod's own init starts");
        }

        return current;
    }

    public static final class BlockBuilder {
        private final Identifier from;

        private BlockBuilder(Identifier from) {
            this.from = Objects.requireNonNull(from);
        }

        public BlockTarget to(Identifier to) {
            return new BlockTarget(from, Objects.requireNonNull(to));
        }
    }

    public static final class BlockTarget {
        private final Identifier from;
        private final Identifier to;
        @Nullable
        private PropertyMapper propertyMapper;
        @Nullable
        private BlockEntityMapper entityMapper;
        @Nullable
        private ToIntFunction<BlockEntity> hiddenSlots;
        @Nullable
        private ItemMapper itemMapper;

        private BlockTarget(Identifier from, Identifier to) {
            this.from = from;
            this.to = to;
        }

        public BlockTarget mapProperties(PropertyMapper mapper) {
            this.propertyMapper = Objects.requireNonNull(mapper);
            return this;
        }

        public BlockTarget mapBlockEntity(BlockEntityMapper mapper) {
            this.entityMapper = Objects.requireNonNull(mapper);
            return this;
        }

        public BlockTarget hiddenSlots(ToIntFunction<BlockEntity> count) {
            this.hiddenSlots = Objects.requireNonNull(count);
            return this;
        }

        public BlockTarget mapItem(ItemMapper mapper) {
            this.itemMapper = Objects.requireNonNull(mapper);
            return this;
        }

        public void register() {
            requireBackend().register(new BlockMigration(from, to, propertyMapper, entityMapper, hiddenSlots, itemMapper));
        }
    }

    public static final class ItemBuilder {
        private final Identifier from;

        private ItemBuilder(Identifier from) {
            this.from = Objects.requireNonNull(from);
        }

        public ItemTarget to(Identifier to) {
            return new ItemTarget(from, Objects.requireNonNull(to));
        }
    }

    public static final class ItemTarget {
        private final Identifier from;
        private final Identifier to;
        @Nullable
        private ItemMapper mapper;

        private ItemTarget(Identifier from, Identifier to) {
            this.from = from;
            this.to = to;
        }

        public ItemTarget mapItem(ItemMapper mapper) {
            this.mapper = Objects.requireNonNull(mapper);
            return this;
        }

        public void register() {
            requireBackend().register(new ItemMigration(from, to, mapper));
        }
    }
}
