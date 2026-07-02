package com.chimericdream.minekea.client.screen.crate;

import com.chimericdream.lib.screen.SimpleInventoryScreen;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrateScreen extends SimpleInventoryScreen<CrateScreenHandler> {
    public CrateScreen(CrateScreenHandler handler, Inventory inventory, Component title) {
        super(handler, CrateBlock.ROW_COUNT, inventory, title);
    }
}
