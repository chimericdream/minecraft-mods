package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BannerPatternLayers.class)
public class BannerPatternsComponentMixin {
    @ModifyConstant(method = "appendTooltip", constant = @Constant(intValue = 6))
    private int getLimit(int curr) {
        return BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
    }
}
