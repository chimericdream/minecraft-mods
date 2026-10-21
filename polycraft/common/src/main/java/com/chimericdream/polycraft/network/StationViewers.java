package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.mixin.PCAbstractFurnaceMenuAccessor;
import com.chimericdream.polycraft.recipe.PinHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Finds who is using a block that has a menu: a furnace-type block or the Crafter. The acting player is the viewer who
 * clicked most recently; with one viewer that is just them, and with none (a hopper feeding a furnace, a redstone pulse
 * to a Crafter nobody has open) there isn't one.
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
        return blockOf(menu).filter(AbstractFurnaceBlockEntity.class::isInstance).map(AbstractFurnaceBlockEntity.class::cast);
    }

    /** The block entity behind a furnace or Crafter menu, when the menu is the server's real one. */
    public static Optional<BlockEntity> blockOf(AbstractContainerMenu menu) {
        Container container = null;
        if (menu instanceof AbstractFurnaceMenu) {
            container = ((PCAbstractFurnaceMenuAccessor) menu).polycraft$container();
        } else if (menu instanceof CrafterMenu crafter) {
            container = crafter.getContainer();
        }

        return container instanceof BlockEntity block ? Optional.of(block) : Optional.empty();
    }

    /** The block behind {@code menu}, if it can have a recipe pinned to it. */
    public static Optional<PinHolder> pinHolderOf(AbstractContainerMenu menu) {
        return blockOf(menu).filter(PinHolder.class::isInstance).map(PinHolder.class::cast);
    }

    /** The player currently using {@code station}, or null when nobody has it open. */
    public static @Nullable ServerPlayer acting(ServerLevel level, BlockEntity station) {
        ServerPlayer best = null;
        long bestClick = -1;
        for (ServerPlayer player : level.players()) {
            Optional<BlockEntity> viewed = blockOf(player.containerMenu);
            if (viewed.isEmpty() || viewed.get() != station) {
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
