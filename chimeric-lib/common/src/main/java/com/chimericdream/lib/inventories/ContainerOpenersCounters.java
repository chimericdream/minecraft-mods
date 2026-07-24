package com.chimericdream.lib.inventories;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Factory for the {@link ContainerOpenersCounter} boilerplate that container block entities all
 * hand-roll identically: an anonymous subclass whose {@code openerCountChanged} fires the vanilla
 * comparator block event and whose {@code isOwnContainer} confirms the player is looking at
 * <em>this</em> container.
 *
 * <p>The menu class is a required parameter, and ownership is confirmed by identity
 * ({@code inventoryAccessor.apply(menu) == owner}) rather than by class alone — this is what stops
 * the copy-paste bug where a block entity checked for the wrong menu type (e.g. the dye station
 * testing {@code ChestMenu}) and therefore never recognised a legitimate viewer.
 */
public final class ContainerOpenersCounters {
    /** Side effect to run when the container opens or closes (e.g. play a sound, set an OPEN state). */
    @FunctionalInterface
    public interface OpenCloseHandler {
        void accept(Level level, BlockPos pos, BlockState state);
    }

    /** Reacts to a change in viewer count (e.g. fire the comparator block event, poke neighbours). */
    @FunctionalInterface
    public interface ViewerCountHandler {
        void accept(Level level, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount);
    }

    private ContainerOpenersCounters() {
    }

    /**
     * Equivalent to {@link #create(Container, Class, Function, OpenCloseHandler, OpenCloseHandler,
     * ViewerCountHandler)} with the plain vanilla viewer-count reaction (fire the comparator block
     * event). Use the six-arg overload when the block needs extra behavior on viewer-count changes
     * (e.g. a trapped-chest-style neighbour update).
     */
    public static ContainerOpenersCounter create(
        Container owner,
        Class<? extends AbstractContainerMenu> menuClass,
        Function<AbstractContainerMenu, Container> inventoryAccessor,
        @Nullable OpenCloseHandler onOpen,
        @Nullable OpenCloseHandler onClose
    ) {
        return create(owner, menuClass, inventoryAccessor, onOpen, onClose,
            (world, pos, state, oldCount, newCount) -> world.blockEvent(pos, state.getBlock(), 1, newCount));
    }

    /**
     * @param owner              the container these openers are counted for; ownership is confirmed by
     *                           reference identity against the open menu's inventory
     * @param menuClass          the menu class this container opens (required)
     * @param inventoryAccessor  reads the backing container out of an instance of {@code menuClass}
     * @param onOpen             side effect when the viewer count goes from 0 to >0, or {@code null}
     * @param onClose            side effect when the viewer count returns to 0, or {@code null}
     * @param onViewerCountChanged reaction to any viewer-count change (must fire the comparator event)
     */
    public static ContainerOpenersCounter create(
        Container owner,
        Class<? extends AbstractContainerMenu> menuClass,
        Function<AbstractContainerMenu, Container> inventoryAccessor,
        @Nullable OpenCloseHandler onOpen,
        @Nullable OpenCloseHandler onClose,
        ViewerCountHandler onViewerCountChanged
    ) {
        return new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                if (onOpen != null) {
                    onOpen.accept(world, pos, state);
                }
            }

            @Override
            protected void onClose(Level world, BlockPos pos, BlockState state) {
                if (onClose != null) {
                    onClose.accept(world, pos, state);
                }
            }

            @Override
            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                onViewerCountChanged.accept(world, pos, state, oldViewerCount, newViewerCount);
            }

            @Override
            public boolean isOwnContainer(Player player) {
                AbstractContainerMenu menu = player.containerMenu;
                return menuClass.isInstance(menu) && inventoryAccessor.apply(menu) == owner;
            }
        };
    }
}
