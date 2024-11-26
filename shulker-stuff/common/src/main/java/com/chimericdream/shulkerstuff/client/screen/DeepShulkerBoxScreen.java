package com.chimericdream.shulkerstuff.client.screen;

import com.chimericdream.lib.screen.SimpleInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class DeepShulkerBoxScreen extends SimpleInventoryScreen<DeepShulkerBoxScreenHandler> {
    public DeepShulkerBoxScreen(DeepShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, 3 + handler.extraRows, inventory, title);
    }
}
