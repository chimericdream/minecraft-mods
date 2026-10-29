package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Whether the player who earned an advancement has it announced in chat ({@code showAdvancementMessages}).
 * The check is in a lambda inside {@code award}, so the target is the synthetic lambda method.
 */
@Mixin(PlayerAdvancements.class)
public abstract class RFT$PlayerAdvancementsMixin {
    @Shadow
    @Final
    private ServerPlayer player;

    @WrapOperation(
        method = "lambda$award$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerRule(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked(player, rule, original.call(rules, rule));
    }
}
