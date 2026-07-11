package com.chimericdream.lib.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

public class SimpleInventoryScreen<Handler extends AbstractContainerMenu> extends AbstractContainerScreen<Handler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

    public SimpleInventoryScreen(Handler handler, int numRows, Inventory inventory, Component title) {
        super(
            handler,
            inventory,
            title,
            114 + numRows * ScreenHelpers.ROW_HEIGHT,
            114 + (numRows * 18)
        );

        this.inventoryLabelY = this.imageHeight - 94;
    }

//    @Override
//    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
//        this.renderBackground(context, mouseX, mouseY, delta);
//        super.render(context, mouseX, mouseY, delta);
//        this.renderTooltip(context, mouseX, mouseY);
//    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
