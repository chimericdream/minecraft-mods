package com.chimericdream.miniblockmerchants.mixin;

import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.registry.ModProfessions;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntity.class)
abstract public class MMZombieVillagerEntityMixin extends MMEntityMixin {
    @Shadow
    private TradeOfferList offerData;

    @Shadow
    private int experience;

    @Shadow
    @Final
    private static TrackedData<VillagerData> VILLAGER_DATA;

    @Inject(method = "setVillagerData", at = @At("TAIL"))
    private void mm$updateVillagerData(VillagerData data, CallbackInfo ci) {
        String profession = data.profession().getIdAsString();

        if (!profession.startsWith(ModInfo.MOD_ID)) {
            return;
        }

        this.dataTracker.set(VILLAGER_DATA, data.withLevel(5));

        this.offerData = ModProfessions.getOffersForProfession(profession);
        this.experience = 250;
    }
}
