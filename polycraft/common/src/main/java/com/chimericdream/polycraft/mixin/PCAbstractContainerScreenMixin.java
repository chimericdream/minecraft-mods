package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.client.ConflictOverlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The picker draws just before the screen queues its item tooltip. Tooltips are drawn last, so anything drawn in a
 * render event after the screen would end up on top of them.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class PCAbstractContainerScreenMixin {
    @Inject(
        method = "extractRenderState",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
    )
    private void polycraft$extractOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        ConflictOverlay.onRender((AbstractContainerScreen<?>) (Object) this, graphics, mouseX, mouseY);
    }
}
