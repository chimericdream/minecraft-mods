package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class CN$LivingEntityMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void cn$onLeashedFishDrown(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (
            self instanceof AbstractFish
                && self instanceof Leashable leashable
                && source.is(DamageTypes.DROWN)
                && leashable.getLeashHolder() instanceof ServerPlayer serverPlayer
        ) {
            CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.FISH_OUT_OF_WATER);
        }
    }
}
