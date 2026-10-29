package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Whether a tamed animal's death message is announced follows its owner's {@code showDeathMessages}, since
 * the message is about the owner's pet. An untamed animal, or one whose owner isn't online, gets the world's
 * value.
 */
@Mixin(TamableAnimal.class)
public abstract class RFT$TamableAnimalMixin {
    @WrapOperation(
        method = "die",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$ownersRule(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked(((TamableAnimal) (Object) this).getOwner(), rule, original.call(rules, rule));
    }
}
