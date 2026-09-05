package com.chimericdream.camelnostrils.menu;

import com.chimericdream.camelnostrils.block.ModBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jspecify.annotations.NonNull;

public class UpsideDownCraftingMenu extends CraftingMenu {
    private final ContainerLevelAccess access;

    public UpsideDownCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(containerId, inventory, access);

        this.access = access;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(this.access, player, ModBlocks.UPSIDE_DOWN_CRAFTING_TABLE.get());
    }
}
