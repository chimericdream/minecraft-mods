package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * How long a player stands in a Nether portal before travelling. Vanilla picks the creative or the default
 * delay rule by the player's abilities, then reads it; the player is the method's {@code entity} argument.
 */
@Mixin(NetherPortalBlock.class)
public abstract class RFT$NetherPortalBlockMixin {
    @WrapOperation(
        method = "getPortalTransitionTime",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerDelay(GameRules rules, GameRule<?> rule, Operation<Object> original, @Local(argsOnly = true) Entity entity) {
        return PlayerRules.resolveUnchecked(entity, rule, original.call(rules, rule));
    }
}
