package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.client.gui.CampfireSneaking;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// MC 26.1.2: the HUD render class here is net.minecraft.client.gui.Gui, not the
// 26.2-era Hud class (which does not exist on this version). extractAirBubbles(...)
// keeps the identical private signature, so only the mixin target changes.
@Mixin(Gui.class)
public abstract class SneakyTweaks$HudMixin {
    @Unique
    private final CampfireSneaking st$campfireSneaking = new CampfireSneaking();

    @Inject(method = "extractAirBubbles", at = @At("TAIL"))
    private void st$extractCampfireGrace(GuiGraphicsExtractor graphics, Player player, int vehicleHearts, int yLineAir, int xRight, CallbackInfo ci) {
        this.st$campfireSneaking.extractFlameTendrils(graphics, player, vehicleHearts, yLineAir, xRight);
    }
}
