package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class HopperItemFilterScreen extends AbstractHopperItemFilterScreen<HopperItemFilterScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/hopper_item_filter.png");

    private static final int IMAGE_HEIGHT = 133;

    public HopperItemFilterScreen(HopperItemFilterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, TEXTURE, IMAGE_HEIGHT);
    }
}
