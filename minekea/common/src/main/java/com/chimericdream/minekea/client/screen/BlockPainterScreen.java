package com.chimericdream.minekea.client.screen;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlockPainterScreen extends AbstractContainerScreen<BlockPainterScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/painter/block_painter.png");
    private final int NUM_ROWS = 2;

    public BlockPainterScreen(BlockPainterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        ++this.imageHeight;
        this.inventoryLabelY = this.imageHeight - 111;
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
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }
}
