package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.ItemMigration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Renames retired block and item IDs inside saved data (NBT), before the game tries to resolve them.
 * <p>
 * Only IDs that are known and safe to rename are touched: block palette names, pending block ticks,
 * and item stacks wherever they sit (containers, inventories, item frames, bundles). Anything else is
 * left exactly as it was. Block entity type IDs are deliberately not handled yet.
 */
public final class IdRewriter {
    public static final IdRewriter EMPTY = new IdRewriter(Map.of(), Map.of());

    private final Map<String, String> blocks;
    private final Map<String, String> items;

    public IdRewriter(Map<String, String> blocks, Map<String, String> items) {
        this.blocks = Map.copyOf(blocks);
        this.items = Map.copyOf(items);
    }

    /**
     * Builds the rules for migrations that can be applied to saved data alone: the old ID is no longer
     * registered (so the data would otherwise be lost), the target is, and the migration needs nothing
     * beyond a rename. A migration with a mapper keeps going through the live conversion path instead.
     */
    public static IdRewriter from(
        Collection<BlockMigration> blockMigrations,
        Collection<ItemMigration> itemMigrations,
        Predicate<Identifier> blockRegistered,
        Predicate<Identifier> itemRegistered
    ) {
        Map<String, String> blocks = new HashMap<>();
        for (BlockMigration migration : blockMigrations) {
            boolean renameOnly = migration.propertyMapper() == null
                && migration.entityMapper() == null
                && migration.hiddenSlots() == null;

            if (renameOnly && !blockRegistered.test(migration.from()) && blockRegistered.test(migration.to())) {
                blocks.put(migration.from().toString(), migration.to().toString());
            }
        }

        Map<String, String> items = new HashMap<>();
        for (ItemMigration migration : itemMigrations) {
            if (migration.mapper() == null && !itemRegistered.test(migration.from()) && itemRegistered.test(migration.to())) {
                items.put(migration.from().toString(), migration.to().toString());
            }
        }

        return new IdRewriter(blocks, items);
    }

    public boolean isEmpty() {
        return blocks.isEmpty() && items.isEmpty();
    }

    @Override
    public String toString() {
        return "IdRewriter{blocks=" + blocks + ", items=" + items + "}";
    }

    /**
     * Rewrites {@code root} in place.
     *
     * @return how many IDs were renamed
     */
    public int rewrite(CompoundTag root) {
        if (isEmpty()) {
            return 0;
        }

        return visitCompound(root);
    }

    private int visit(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            return visitCompound(compound);
        }

        if (tag instanceof ListTag list) {
            int count = 0;
            for (Tag element : list) {
                count += visit(element);
            }

            return count;
        }

        return 0;
    }

    private int visitCompound(CompoundTag tag) {
        int count = 0;

        // An item stack: a string "id" next to a numeric "count".
        if (!items.isEmpty() && tag.get("count") instanceof NumericTag) {
            String replacement = tag.getString("id").map(items::get).orElse(null);
            if (replacement != null) {
                tag.putString("id", replacement);
                count++;
            }
        }

        for (Map.Entry<String, Tag> entry : tag.entrySet()) {
            Tag child = entry.getValue();

            if (!blocks.isEmpty() && child instanceof ListTag list) {
                switch (entry.getKey()) {
                    case "palette" -> {
                        count += renameIn(list, "Name");
                        continue;
                    }
                    case "block_ticks" -> count += renameIn(list, "i");
                    default -> {
                    }
                }
            }

            count += visit(child);
        }

        return count;
    }

    // Renames the block ID stored under `key` in each compound of the list; non-compound entries (such as
    // the strings of a biome palette) are skipped.
    private int renameIn(ListTag list, String key) {
        int count = 0;
        for (Tag element : list) {
            if (!(element instanceof CompoundTag compound)) {
                continue;
            }

            String replacement = compound.getString(key).map(blocks::get).orElse(null);
            if (replacement != null) {
                compound.putString(key, replacement);
                count++;
            }
        }

        return count;
    }
}
