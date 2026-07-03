package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoomScreen.class)
public class LoomMenuMixin {
    @ModifyConstant(method = "containerChanged", constant = @Constant(intValue = 6))
    public int getLimit(int constant) {
        return BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
    }
}
