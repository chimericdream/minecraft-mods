package com.chimericdream.shulkerstuff.client.screen;

import com.chimericdream.shulkerstuff.ModInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class DyeStationScreen extends AbstractContainerScreen<DyeStationScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/block/dye_station.png");

    public DyeStationScreen(DyeStationScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
