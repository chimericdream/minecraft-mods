package com.chimericdream.opus.item;

import com.chimericdream.opus.component.TomeContentsComponent;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Sneak-dropping a guide that was turned out of a Book of Books takes that guide out of the set: the dropped item
 * becomes a plain copy of the guide, and the player gets the Book of Books back (minus that guide) in the slot the
 * guide came from.
 */
public final class TomeEvents {
    private TomeEvents() {
    }

    public static void init() {
        PlayerEvent.DROP_ITEM.register(TomeEvents::onDrop);
    }

    private static EventResult onDrop(Player player, ItemEntity entity) {
        if (!(player instanceof ServerPlayer serverPlayer) || !serverPlayer.isShiftKeyDown() || !serverPlayer.isAlive()) {
            return EventResult.pass();
        }

        TomeContentsComponent contents = TomeStacks.transformedContents(entity.getItem());
        if (contents == null) {
            return EventResult.pass();
        }

        entity.setItem(TomeStacks.book(contents.books().get(contents.selected())));
        giveBack(serverPlayer, TomeStacks.tome(contents.without(contents.selected())));

        return EventResult.pass();
    }

    /** Puts {@code tome} in the selected hotbar slot, which the drop just emptied, or anywhere it fits. */
    private static void giveBack(ServerPlayer player, ItemStack tome) {
        Inventory inventory = player.getInventory();

        if (inventory.getSelectedItem().isEmpty()) {
            inventory.setItem(inventory.getSelectedSlot(), tome);
        } else if (!inventory.add(tome)) {
            player.drop(tome, false);
        }
    }
}
