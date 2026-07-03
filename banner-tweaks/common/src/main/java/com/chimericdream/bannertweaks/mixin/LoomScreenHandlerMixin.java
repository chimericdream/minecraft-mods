package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoomMenu.class)
public class LoomScreenHandlerMixin {
    @ModifyConstant(method = "slotsChanged", constant = @Constant(intValue = 6))
    public int getLimit(int constant) {
        return BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
    }
}
