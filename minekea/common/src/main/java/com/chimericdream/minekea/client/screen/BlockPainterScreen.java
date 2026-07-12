package com.chimericdream.minekea.client.screen;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class BlockPainterScreen extends AbstractContainerScreen<BlockPainterScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/painter/block_painter.png");
    private final int NUM_ROWS = 2;

    public BlockPainterScreen(BlockPainterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 167);
        this.inventoryLabelY = this.imageHeight - 111;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }
}
