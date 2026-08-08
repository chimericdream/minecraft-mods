package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VillagerMakeLove.class)
public abstract class VTVillagerMakeLoveMixin {
    @ModifyConstant(method = "breed", constant = @Constant(intValue = -24000))
    private int vt$modifyGrowUpTime(int constant) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (config.enableGrowUpTimeOverride) {
            return -config.growUpTime;
        }

        return constant;
    }
}
