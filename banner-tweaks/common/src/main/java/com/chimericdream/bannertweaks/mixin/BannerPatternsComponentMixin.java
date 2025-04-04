package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.component.type.BannerPatternsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BannerPatternsComponent.class)
public class BannerPatternsComponentMixin {
    @ModifyConstant(method = "appendTooltip", constant = @Constant(intValue = 6))
    private int getLimit(int curr) {
        return BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
    }
}
