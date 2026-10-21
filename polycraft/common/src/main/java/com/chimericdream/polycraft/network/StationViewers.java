package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.mixin.PCAbstractFurnaceMenuAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Finds who is using a furnace-type block. The acting player is the viewer who clicked most recently; with one viewer
 * that is just them, and with none (a hopper feeding a furnace) there isn't one.
 */
public final class StationViewers {
    private static final Map<AbstractContainerMenu, Long> LAST_CLICK = new WeakHashMap<>();
    private static long clicks;

    private StationViewers() {
    }

    /** Notes that someone just clicked in {@code menu}. */
    public static void touch(AbstractContainerMenu menu) {
        LAST_CLICK.put(menu, ++clicks);
    }

    /** The furnace-type block entity behind {@code menu}, if it is a furnace menu on the server. */
    public static Optional<AbstractFurnaceBlockEntity> furnaceOf(AbstractContainerMenu menu) {
        if (!(menu instanceof AbstractFurnaceMenu)) {
            return Optional.empty();
        }

        Container container = ((PCAbstractFurnaceMenuAccessor) menu).polycraft$container();
        return container instanceof AbstractFurnaceBlockEntity furnace ? Optional.of(furnace) : Optional.empty();
    }

    /** The player currently using {@code furnace}, or null when nobody has it open. */
    public static @Nullable ServerPlayer acting(ServerLevel level, AbstractFurnaceBlockEntity furnace) {
        ServerPlayer best = null;
        long bestClick = -1;
        for (ServerPlayer player : level.players()) {
            Optional<AbstractFurnaceBlockEntity> viewed = furnaceOf(player.containerMenu);
            if (viewed.isEmpty() || viewed.get() != furnace) {
                continue;
            }

            long click = LAST_CLICK.getOrDefault(player.containerMenu, 0L);
            if (click > bestClick) {
                best = player;
                bestClick = click;
            }
        }

        return best;
    }
}
