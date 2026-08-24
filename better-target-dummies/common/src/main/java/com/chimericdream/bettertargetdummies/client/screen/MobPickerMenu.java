package com.chimericdream.bettertargetdummies.client.screen;

import com.chimericdream.bettertargetdummies.ModInfo;
import com.chimericdream.bettertargetdummies.item.DummySpawnEggItem;
import com.chimericdream.bettertargetdummies.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Carries no slots -- it only exists so the server can safely tell the client to open the mob
 * picker {@link MobPickerScreen} (common item code can't reference client Screen classes directly).
 * The actual selection travels back through vanilla's container-button-click protocol: the client
 * sends the chosen entity type's registry id as the button id, and {@link #clickMenuButton} looks
 * it back up and applies it to whichever hand is holding a Dummy Spawn Egg.
 */
public class MobPickerMenu extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/items/mob_picker");

    public MobPickerMenu(int syncId, Inventory playerInventory) {
        super(ModMenus.MOB_PICKER_MENU.get(), syncId);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.byId(id);
        if (type == null || !type.canSummon()) {
            return false;
        }

        ItemStack dummyEgg = findHeldDummyEgg(player);
        if (dummyEgg.isEmpty()) {
            return false;
        }

        DummySpawnEggItem.setBoundMobType(dummyEgg, type);
        player.sendOverlayMessage(Component.translatable(ModInfo.MOD_ID + ".dummy_spawn_egg.bound", type.getDescription()));
        return true;
    }

    private static @NotNull ItemStack findHeldDummyEgg(Player player) {
        if (player.getMainHandItem().is(ModItems.DUMMY_SPAWN_EGG.get())) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().is(ModItems.DUMMY_SPAWN_EGG.get())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }
}
