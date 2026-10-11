package com.chimericdream.allhallowssteve.client.screen;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class CiderPressScreen extends AbstractContainerScreen<CiderPressScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/block/cider_press.png");

    // The filled-in progress arrow and fuel bar sit to the right of the panel in the same texture.
    private static final int ARROW_X = 80;
    private static final int ARROW_Y = 35;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;

    private static final int FUEL_X = 17;
    private static final int FUEL_Y = 56;
    private static final int FUEL_WIDTH = 18;
    private static final int FUEL_HEIGHT = 4;
    private static final int FUEL_U = 176;
    private static final int FUEL_V = 18;

    public CiderPressScreen(CiderPressScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        int fuel = this.menu.getFuel();
        int fuelLength = Mth.clamp((FUEL_WIDTH * fuel + CiderPressBlockEntity.MAX_CHARGES - 1) / CiderPressBlockEntity.MAX_CHARGES, 0, FUEL_WIDTH);
        if (fuelLength > 0) {
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + FUEL_X, y + FUEL_Y, FUEL_U, FUEL_V, fuelLength, FUEL_HEIGHT, 256, 256);
        }

        int pressTime = this.menu.getPressTime();
        if (pressTime > 0) {
            int progress = (int) (ARROW_WIDTH * (1.0F - (float) pressTime / CiderPressBlockEntity.PRESS_TIME));
            if (progress > 0) {
                context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + ARROW_X, y + ARROW_Y, ARROW_U, ARROW_V, progress, ARROW_HEIGHT, 256, 256);
            }
        }
    }
}
