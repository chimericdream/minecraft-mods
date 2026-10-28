package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.ForwardingSettings;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Hooks conversion into the game. Placed blocks are found on chunk load (the loader-specific chunk
 * events call {@link #onChunkLoaded}), reported to a {@link ConversionQueue}, and converted a few at a
 * time each server tick. Held items convert on login, and dropped items and item frames when they load (the loader-specific entity events call {@link #onEntityLoaded}).
 */
public final class MigrationEvents {
    private static final MigrationLog LOG = new MigrationLog(ForwardingAddressMod.LOGGER);
    private static final MigrationRegistry REGISTRY = MigrationRegistry.INSTANCE;
    private static final BlockConverter BLOCKS = new BlockConverter(REGISTRY, LOG);
    private static final ItemConverter ITEMS = new ItemConverter(REGISTRY, LOG);
    private static final ChunkScanner SCANNER = new ChunkScanner(REGISTRY);
    private static final ConversionQueue<ResourceKey<Level>, ChunkPos, BlockPos> QUEUE = new ConversionQueue<>();

    private MigrationEvents() {
    }

    public static BlockConverter blocks() {
        return BLOCKS;
    }

    public static ItemConverter items() {
        return ITEMS;
    }

    public static ConversionQueue<ResourceKey<Level>, ChunkPos, BlockPos> queue() {
        return QUEUE;
    }

    public static void init() {
        TickEvent.SERVER_LEVEL_POST.register(MigrationEvents::onLevelTick);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(level -> QUEUE.clear(level.dimension()));
        LifecycleEvent.SERVER_STOPPED.register(server -> QUEUE.clear());
        PlayerEvent.PLAYER_JOIN.register(MigrationEvents::onPlayerJoin);
    }

    /**
     * Called by each loader's chunk-load event. Only reports the chunk; nothing is changed here, and
     * this may run off the server thread.
     */
    public static void onChunkLoaded(ServerLevel level, ChunkPos chunk) {
        if (REGISTRY.isEmpty()) {
            return;
        }

        QUEUE.enqueueChunk(level.dimension(), chunk);
    }

    private static void onLevelTick(ServerLevel level) {
        if (REGISTRY.isEmpty()) {
            return;
        }

        drain(level, ForwardingSettings.get().maxConversionsPerTick);
    }

    /**
     * Scans reported chunks and converts up to {@code budget} positions in one level.
     *
     * @return how many positions were handled
     */
    public static int drain(ServerLevel level, int budget) {
        return QUEUE.drain(
            level.dimension(),
            budget,
            (key, chunk) -> SCANNER.scan(level, chunk),
            (key, pos) -> convertAt(level, pos)
        );
    }

    /**
     * Converts the block at {@code pos}, then any migrated items in its container.
     */
    public static void convertAt(ServerLevel level, BlockPos pos) {
        BLOCKS.convert(level, pos);

        if (REGISTRY.hasItemMigrations()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof Container container) {
                ITEMS.convertContainer(container);
            }
        }
    }

    private static void onPlayerJoin(ServerPlayer player) {
        convertPlayerItems(player);
    }

    /**
     * Converts the player's inventory (armor and offhand included) and ender chest.
     */
    public static void convertPlayerItems(ServerPlayer player) {
        if (!REGISTRY.hasItemMigrations()) {
            return;
        }

        ITEMS.convertContainer(player.getInventory());
        ITEMS.convertContainer(player.getEnderChestInventory());
    }

    /**
     * Called by each loader's entity-load event, which must fire for entities read from disk as well as
     * freshly spawned ones (Architectury's {@code EntityEvent.ADD} does not on Fabric).
     */
    public static void onEntityLoaded(Entity entity, Level level) {
        if (level.isClientSide() || !REGISTRY.hasItemMigrations()) {
            return;
        }

        if (entity instanceof ItemEntity itemEntity) {
            ItemStack original = itemEntity.getItem();
            ItemStack converted = ITEMS.convert(original);
            if (converted != original) {
                itemEntity.setItem(converted);
            }
        } else if (entity instanceof ItemFrame frame) {
            ItemStack original = frame.getItem();
            ItemStack converted = ITEMS.convert(original);
            if (converted != original) {
                frame.setItem(converted);
            }
        }
    }
}
