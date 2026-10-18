package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.trade.LockableOffer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws the locked trade previews (see {@code LockedTradesPayload}) greyed out, with a tooltip line naming the
 * level that unlocks them, and keeps them from being selected.
 */
@Mixin(MerchantScreen.class)
public abstract class VTMerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {
    // Layout of the trade buttons, from MerchantScreen.
    @Unique
    private static final int VT$ROWS = 7;
    @Unique
    private static final int VT$BUTTON_X = 5;
    @Unique
    private static final int VT$BUTTON_Y = 18;
    @Unique
    private static final int VT$BUTTON_WIDTH = 88;
    @Unique
    private static final int VT$BUTTON_HEIGHT = 20;

    // A dark, mostly opaque wash so locked rows clearly read as unavailable; the items stay faintly visible.
    @Unique
    private static final int VT$LOCKED_OVERLAY = 0xB8303030;

    @Shadow
    private int shopItem;

    @Shadow
    private int scrollOff;

    @Unique
    private int vt$selectedOffer = 0;

    public VTMerchantScreenMixin(MerchantMenu menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    // The row buttons are plain vanilla buttons, so a click on a locked one still lands here: undo the
    // selection change and don't tell the server about it.
    @Inject(method = "postButtonClick", at = @At("HEAD"), cancellable = true)
    private void vt$ignoreLockedTrades(CallbackInfo ci) {
        MerchantOffers offers = this.menu.getOffers();

        if (this.shopItem >= 0 && this.shopItem < offers.size() && vt$isLocked(offers.get(this.shopItem))) {
            this.shopItem = this.vt$selectedOffer;
            ci.cancel();
        } else {
            this.vt$selectedOffer = this.shopItem;
        }
    }

    @Inject(method = "extractContents", at = @At("TAIL"))
    private void vt$markLockedTrades(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        MerchantOffers offers = this.menu.getOffers();
        boolean startedStratum = false;

        for (int row = 0; row < VT$ROWS; row++) {
            int offerIndex = row + this.scrollOff;
            if (offerIndex >= offers.size()) {
                break;
            }

            MerchantOffer offer = offers.get(offerIndex);
            if (!vt$isLocked(offer)) {
                continue;
            }

            if (!startedStratum) {
                // Everything vanilla drew for the rows, items included, has to end up under the overlay.
                graphics.nextStratum();
                startedStratum = true;
            }

            int x = this.leftPos + VT$BUTTON_X;
            int y = this.topPos + VT$BUTTON_Y + row * VT$BUTTON_HEIGHT;
            graphics.fill(x, y, x + VT$BUTTON_WIDTH, y + VT$BUTTON_HEIGHT, VT$LOCKED_OVERLAY);

            if (mouseX >= x && mouseX < x + VT$BUTTON_WIDTH && mouseY >= y && mouseY < y + VT$BUTTON_HEIGHT) {
                vt$extractLockedTooltip(graphics, offer, mouseX - x, mouseX, mouseY);
            }
        }
    }

    /**
     * Vanilla's own hover tooltip for the row has already been queued by now, and the first one queued wins,
     * so this has to replace it. Which item it describes follows vanilla's hit areas for the row.
     */
    @Unique
    private void vt$extractLockedTooltip(GuiGraphicsExtractor graphics, MerchantOffer offer, int xInRow, int mouseX, int mouseY) {
        ItemStack hovered = ItemStack.EMPTY;

        if (xInRow < 20) {
            hovered = offer.getCostA();
        } else if (xInRow > 30 && xInRow < 50) {
            hovered = offer.getCostB();
        } else if (xInRow > 65) {
            hovered = offer.getResult();
        }

        if (hovered.isEmpty()) {
            return;
        }

        List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(this.minecraft, hovered));
        int level = ((LockableOffer) offer).vt$getUnlockLevel();
        lines.add(Component.translatable(
            "tooltip.villagertweaks.available_at_level",
            Component.translatable("merchant.level." + level)
        ).withStyle(ChatFormatting.YELLOW));

        List<FormattedCharSequence> visualLines = lines.stream().map(Component::getVisualOrderText).toList();

        graphics.setTooltipForNextFrame(
            this.font,
            visualLines,
            hovered.getTooltipImage(),
            DefaultTooltipPositioner.INSTANCE,
            mouseX,
            mouseY,
            true,
            null
        );
    }

    @Unique
    private static boolean vt$isLocked(MerchantOffer offer) {
        return offer instanceof LockableOffer lockable && lockable.vt$isLocked();
    }
}
