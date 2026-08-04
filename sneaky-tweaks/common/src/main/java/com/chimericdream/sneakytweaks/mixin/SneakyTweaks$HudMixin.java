package com.chimericdream.sneakytweaks.mixin;

import com.chimericdream.sneakytweaks.client.gui.CampfireSneaking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class SneakyTweaks$HudMixin {
    @Unique
    private final CampfireSneaking st$campfireSneaking = new CampfireSneaking();

    @Inject(method = "extractAirBubbles", at = @At("TAIL"))
    private void st$extractCampfireGrace(GuiGraphicsExtractor graphics, Player player, int vehicleHearts, int yLineAir, int xRight, CallbackInfo ci) {
        this.st$campfireSneaking.extractFlameTendrils(graphics, player, vehicleHearts, yLineAir, xRight);
    }
}
