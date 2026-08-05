package com.chimericdream.sneakytweaks.client.gui;

import com.chimericdream.sneakytweaks.ModInfo;
import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.campfire.CampfireSneakingLogic;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CampfireSneaking {
    private static final Identifier CAMPFIRE_SPRITE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "hud/campfire_full");
    private static final Identifier CAMPFIRE_BURSTING_SPRITE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "hud/campfire_bursting");

    private int lastEmberPopSoundPlayed;

    public void extractFlameTendrils(final GuiGraphicsExtractor graphics, final Player player, final int vehicleHearts, int yLineAir, final int xRight) {
        SneakyTweaksConfig config = SneakyTweaksConfig.HANDLER.instance();
        if (!config.enableCampfireSneaking) {
            return;
        }

        int maxGraceTicks = config.campfireGraceTicks;
        int currentGraceTicks = Math.clamp((long) CampfireGraceHolder.getCampfireGraceTicks(player), 0, maxGraceTicks);
        boolean isDraining = player.isCrouching() && CampfireSneakingLogic.isOnLitCampfire(player);

        if (isDraining || currentGraceTicks < maxGraceTicks) {
            yLineAir = this.getGraceBarYLine(vehicleHearts, yLineAir);
            int fullEmbers = getCurrentGraceEmber(currentGraceTicks, maxGraceTicks, -getHalfEmberTickDuration(maxGraceTicks));
            int burstingEmberPosition = getCurrentGraceEmber(currentGraceTicks, maxGraceTicks, 0);
            int emptyEmbers = 10 - getCurrentGraceEmber(currentGraceTicks, maxGraceTicks, getEmptyEmberDelayDuration(currentGraceTicks, isDraining));
            boolean isBurstingEmber = fullEmbers != burstingEmberPosition;
            if (!isDraining) {
                this.lastEmberPopSoundPlayed = 0;
            }

            for (int ember = 1; ember <= 10; ++ember) {
                int emberXPos = xRight - (ember - 1) * 8 - 9;
                if (ember <= fullEmbers) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CAMPFIRE_SPRITE, emberXPos, yLineAir, 9, 9);
                } else if (isBurstingEmber && ember == burstingEmberPosition && isDraining) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CAMPFIRE_BURSTING_SPRITE, emberXPos, yLineAir, 9, 9);
                    this.playEmberPoppedSound(ember, player, emptyEmbers);
                }
            }
        }
    }

    private int getVisibleVehicleHeartRows(final int hearts) {
        return (int) Math.ceil((double) hearts / (double) 10.0F);
    }

    private int getGraceBarYLine(final int vehicleHearts, int yLineAir) {
        int rowOffset = this.getVisibleVehicleHeartRows(vehicleHearts) - 1;
        yLineAir -= rowOffset * 10;
        return yLineAir;
    }

    private static int getCurrentGraceEmber(final int currentGraceTicks, final int maxGraceTicks, final float tickOffset) {
        return Mth.ceil(((float) currentGraceTicks + tickOffset) * 10.0F / (float) maxGraceTicks);
    }

    private static float getHalfEmberTickDuration(final int maxGraceTicks) {
        return (float) maxGraceTicks / 10.0F / 2.0F;
    }

    private static int getEmptyEmberDelayDuration(final int currentGraceTicks, final boolean isDraining) {
        return currentGraceTicks != 0 && isDraining ? 1 : 0;
    }

    private void playEmberPoppedSound(final int ember, final Player player, final int emptyEmbers) {
        if (this.lastEmberPopSoundPlayed != ember) {
            float soundVolume = 0.5F + 0.1F * (float) Math.max(0, emptyEmbers - 3 + 1);
            float soundPitch = 1.0F + 0.1F * (float) Math.max(0, emptyEmbers - 5 + 1);
            player.playSound(SoundEvents.FIRE_EXTINGUISH, soundVolume, soundPitch);
            this.lastEmberPopSoundPlayed = ember;
        }
    }
}
