package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Whether a player is sent command feedback ({@code sendCommandFeedback}). Vanilla reads it in the anonymous
 * {@code CommandSource} a {@code ServerPlayer} hands to the command system, in {@code acceptsSuccess}. The
 * anonymous class keeps a reference to its outer player in the synthetic field {@code this$0}.
 */
@Mixin(targets = "net.minecraft.server.level.ServerPlayer$3")
public abstract class RFT$PlayerCommandSourceMixin {
    @Shadow
    @Final
    ServerPlayer this$0;

    @WrapOperation(
        method = "acceptsSuccess",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerFeedback(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked(this$0, rule, original.call(rules, rule));
    }
}
