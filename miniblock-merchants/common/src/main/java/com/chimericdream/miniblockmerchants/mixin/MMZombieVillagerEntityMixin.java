package com.chimericdream.miniblockmerchants.mixin;

import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.registry.ModProfessions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
abstract public class MMZombieVillagerEntityMixin extends MMEntityMixin {
    @Shadow
    private MerchantOffers tradeOffers;

    @Shadow
    private int villagerXp;

    @Shadow
    @Final
    private static EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;

    @Inject(method = "setVillagerData", at = @At("TAIL"))
    private void mm$updateVillagerData(VillagerData data, CallbackInfo ci) {
        String profession = data.profession().getRegisteredName();

        if (!profession.startsWith(ModInfo.MOD_ID)) {
            return;
        }

        this.entityData.set(DATA_VILLAGER_DATA, data.withLevel(5));

        this.tradeOffers = ModProfessions.getOffersForProfession(profession);
        this.villagerXp = 250;
    }
}
