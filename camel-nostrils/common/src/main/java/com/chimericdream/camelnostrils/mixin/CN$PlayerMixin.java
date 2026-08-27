package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import com.chimericdream.camelnostrils.entity.fish.ZombieCod;
import com.chimericdream.camelnostrils.entity.fish.ZombieSalmon;
import com.chimericdream.camelnostrils.entity.fish.ZombieTropicalFish;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CN$PlayerMixin {
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void cn$onHurtByZombieFish(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        Player self = (Player) (Object) this;

        if (!self.isInvulnerableTo(level, source)) {
            if (
                self instanceof ServerPlayer serverPlayer
                    && (source.getEntity() instanceof ZombieCod
                    || source.getEntity() instanceof ZombieSalmon
                    || source.getEntity() instanceof ZombieTropicalFish)
            ) {
                CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.FASTER_THAN_A_SNAIL);
            }
        }
    }
}
