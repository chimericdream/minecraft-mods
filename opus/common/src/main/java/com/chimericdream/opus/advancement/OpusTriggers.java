package com.chimericdream.opus.advancement;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.component.TomeContentsComponent;
import com.chimericdream.opus.item.OpusItems;
import com.chimericdream.opus.item.TomeStacks;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OpusTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(ModInfo.MOD_ID, Registries.TRIGGER_TYPE);

    public static final RegistrySupplier<TomeBooksTrigger> TOME_BOOKS = TRIGGERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_books"),
        TomeBooksTrigger::new
    );

    /** How often, in ticks, a player's inventory is checked. Quick enough that nobody notices the delay. */
    private static final int CHECK_INTERVAL = 20;

    private OpusTriggers() {
    }

    public static void init() {
        TRIGGERS.register();

        TickEvent.PLAYER_POST.register(OpusTriggers::checkInventory);
    }

    private static void checkInventory(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        int most = 0;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(OpusItems.BOOK_OF_BOOKS.get())) {
                TomeContentsComponent contents = TomeStacks.contentsOf(stack);
                if (contents != null) {
                    most = Math.max(most, contents.books().size());
                }
            }
        }

        if (most > 0) {
            TOME_BOOKS.get().trigger(serverPlayer, most);
        }
    }
}
