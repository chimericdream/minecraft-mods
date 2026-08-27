package com.chimericdream.hopperxtreme.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Shared rendering logic for every Hopper Item Filter tier's screen. Every tier's background is
 * 176 wide -- only the height (tiers with more filter rows need more room above the player inventory),
 * the texture, and the filter slot positions baked into it differ.
 */
public abstract class AbstractHopperItemFilterScreen<T extends AbstractHopperItemFilterScreenHandler> extends AbstractContainerScreen<T> {
    private final Identifier texture;

    protected AbstractHopperItemFilterScreen(T handler, Inventory inventory, Component title, Identifier texture, int imageHeight) {
        super(handler, inventory, title, DEFAULT_IMAGE_WIDTH, imageHeight);
        this.texture = texture;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
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
