package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class CN$EntityMixin {
    @Inject(
        method = "interact",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Leashable;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V",
            ordinal = 1
        )
    )
    private void cn$onLeashAttachedToPlayer(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        Entity self = (Entity) (Object) this;

        if (self instanceof AbstractFish && player instanceof ServerPlayer serverPlayer) {
            CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.FISH_WALKER);
        }
    }
}
