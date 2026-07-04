package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.tag.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantOffer.class)
public class VTTradeOfferMixin {
    @Shadow
    private @Final
    @Mutable int maxUses;
    @Shadow
    private @Mutable int uses;
    @Shadow
    private int demand;
    @Shadow
    private @Final ItemStack result;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void forceHighMaxUseCount(CallbackInfo ci) {
        if (this.result.is(ModTags.IGNORED_ITEMS)) {
            return;
        }

        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (config.enableMaxTradeOverride) {
            if (config.maxTradesOverrideAmount > -1) {
                this.maxUses = config.maxTradesOverrideAmount;
            }
        }

        if (!config.enableDemandBonus) {
            this.demand = 0;
        }
    }

    @Inject(method = "updateDemand", at = @At("RETURN"))
    public void resetDemandBonus(CallbackInfo ci) {
        if (this.result.is(ModTags.IGNORED_ITEMS)) {
            return;
        }

        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (!config.enableDemandBonus) {
            this.demand = 0;
        }
    }

    @Inject(method = "increaseUses", at = @At("TAIL"))
    public void checkInfiniteUses(CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (config.enableMaxTradeOverride && config.maxTradesOverrideAmount == -1) {
            --this.uses;
        }
    }
}
