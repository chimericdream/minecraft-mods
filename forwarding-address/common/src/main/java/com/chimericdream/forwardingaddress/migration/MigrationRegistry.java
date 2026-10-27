package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.ItemMigration;
import com.chimericdream.forwardingaddress.api.MigrationBackend;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The one registry every way of declaring a migration writes into. IDs are resolved to real blocks and
 * items lazily, on first use after the game registries are populated, so a migration can be declared
 * before its target mod has registered anything.
 */
public final class MigrationRegistry implements MigrationBackend {
    public static final MigrationRegistry INSTANCE = new MigrationRegistry();

    private final Map<Identifier, BlockMigration> blocks = new LinkedHashMap<>();
    private final Map<Identifier, ItemMigration> items = new LinkedHashMap<>();
    // Block migrations also convert the block's item. An explicit item migration for the same ID wins.
    private final Map<Identifier, ItemMigration> impliedItems = new LinkedHashMap<>();
    private final MigrationLog log;

    @Nullable
    private volatile Resolved resolved;

    public MigrationRegistry() {
        this(new MigrationLog(ForwardingAddressMod.LOGGER));
    }

    public MigrationRegistry(MigrationLog log) {
        this.log = log;
    }

    @Override
    public synchronized void register(BlockMigration migration) {
        if (blocks.containsKey(migration.from())) {
            log.error("duplicate-block:" + migration.from(), "A block migration for {} is already declared; ignoring the new one (-> {})", migration.from(), migration.to());
            return;
        }

        blocks.put(migration.from(), migration);
        impliedItems.putIfAbsent(migration.from(), new ItemMigration(migration.from(), migration.to(), migration.itemMapper()));
        resolved = null;
    }

    @Override
    public synchronized void register(ItemMigration migration) {
        if (items.containsKey(migration.from())) {
            log.error("duplicate-item:" + migration.from(), "An item migration for {} is already declared; ignoring the new one (-> {})", migration.from(), migration.to());
            return;
        }

        items.put(migration.from(), migration);
        resolved = null;
    }

    public synchronized boolean isEmpty() {
        return blocks.isEmpty() && items.isEmpty();
    }

    public synchronized boolean hasItemMigrations() {
        return !items.isEmpty() || !impliedItems.isEmpty();
    }

    public synchronized boolean hasBlockMigrations() {
        return !blocks.isEmpty();
    }

    /**
     * @return the migration for placed blocks of this type, or {@code null} if there isn't one
     */
    @Nullable
    public ResolvedBlock forBlock(Block block) {
        return resolved().blocks.get(block);
    }

    @Nullable
    public ResolvedItem forItem(Item item) {
        return resolved().items.get(item);
    }

    /**
     * @return whether any placed block of this type is waiting for conversion; used to skip chunk
     * sections cheaply by their palette
     */
    public boolean isMigratedBlock(Block block) {
        return resolved().blocks.containsKey(block);
    }

    private Resolved resolved() {
        Resolved current = resolved;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (resolved == null) {
                resolved = resolve();
            }

            return resolved;
        }
    }

    private Resolved resolve() {
        Map<Block, ResolvedBlock> resolvedBlocks = new IdentityHashMap<>();
        Map<Item, ResolvedItem> resolvedItems = new IdentityHashMap<>();

        for (BlockMigration migration : blocks.values()) {
            Block from = BuiltInRegistries.BLOCK.getOptional(migration.from()).orElse(null);
            if (from == null) {
                log.info("missing-block:" + migration.from(), "Block migration {} -> {} does not apply: the old block is not registered", migration.from(), migration.to());
                continue;
            }

            Block to = BuiltInRegistries.BLOCK.getOptional(migration.to()).orElse(null);
            if (to == null) {
                log.warn("missing-block-target:" + migration.to(), "Skipping block migration {} -> {}: the target block is not installed", migration.from(), migration.to());
                continue;
            }

            resolvedBlocks.put(from, new ResolvedBlock(migration, from, to));
        }

        // Explicit item migrations take precedence over the ones implied by block migrations.
        Map<Identifier, ItemMigration> allItems = new LinkedHashMap<>(impliedItems);
        allItems.putAll(items);

        for (ItemMigration migration : allItems.values()) {
            Item from = BuiltInRegistries.ITEM.getOptional(migration.from()).orElse(null);
            if (from == null) {
                log.info("missing-item:" + migration.from(), "Item migration {} -> {} does not apply: the old item is not registered", migration.from(), migration.to());
                continue;
            }

            Item to = BuiltInRegistries.ITEM.getOptional(migration.to()).orElse(null);
            if (to == null) {
                log.warn("missing-item-target:" + migration.to(), "Skipping item migration {} -> {}: the target item is not installed", migration.from(), migration.to());
                continue;
            }

            resolvedItems.put(from, new ResolvedItem(migration, from, to));
        }

        return new Resolved(resolvedBlocks, resolvedItems);
    }

    public record ResolvedBlock(BlockMigration migration, Block from, Block to) {
    }

    public record ResolvedItem(ItemMigration migration, Item from, Item to) {
    }

    private record Resolved(Map<Block, ResolvedBlock> blocks, Map<Item, ResolvedItem> items) {
    }
}
