package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FilteredHopperScreen extends AbstractContainerScreen<FilteredHopperScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/filtered_hopper.png");

    public FilteredHopperScreen(FilteredHopperScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x,
            y,
            0,
            0,
            imageWidth,
            imageHeight,
            256,
            256
        );
    }
}
