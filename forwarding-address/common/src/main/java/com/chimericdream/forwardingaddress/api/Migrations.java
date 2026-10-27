package com.chimericdream.forwardingaddress.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
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
 *
 * <p>Declaring doesn't depend on load order: if your mod initializes before Forwarding Address, the
 * migration is held and handed over as soon as Forwarding Address starts.
 */
public final class Migrations {
    /**
     * Bumped when a change to this package breaks existing callers.
     */
    public static final int API_VERSION = 1;

    @Nullable
    private static volatile MigrationBackend backend;

    // Declarations made before the backend exists, in the order they were made.
    private static final List<Consumer<MigrationBackend>> WAITING = new CopyOnWriteArrayList<>();

    private Migrations() {
    }

    @ApiStatus.Internal
    public static void installBackend(MigrationBackend newBackend) {
        Objects.requireNonNull(newBackend);

        synchronized (WAITING) {
            backend = newBackend;
            for (Consumer<MigrationBackend> declaration : WAITING) {
                declaration.accept(newBackend);
            }
            WAITING.clear();
        }
    }

    @ApiStatus.Internal
    public static int waitingCount() {
        return WAITING.size();
    }

    /**
     * Forgets the installed backend and any waiting declarations. For tests only.
     */
    @ApiStatus.Internal
    public static void reset() {
        synchronized (WAITING) {
            backend = null;
            WAITING.clear();
        }
    }

    public static BlockBuilder block(Identifier from) {
        return new BlockBuilder(from);
    }

    public static ItemBuilder item(Identifier from) {
        return new ItemBuilder(from);
    }

    private static void declare(Consumer<MigrationBackend> declaration) {
        synchronized (WAITING) {
            MigrationBackend current = backend;
            if (current == null) {
                WAITING.add(declaration);
            } else {
                declaration.accept(current);
            }
        }
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
            BlockMigration migration = new BlockMigration(from, to, propertyMapper, entityMapper, hiddenSlots, itemMapper);
            declare(target -> target.register(migration));
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
            ItemMigration migration = new ItemMigration(from, to, mapper);
            declare(target -> target.register(migration));
        }
    }
}
