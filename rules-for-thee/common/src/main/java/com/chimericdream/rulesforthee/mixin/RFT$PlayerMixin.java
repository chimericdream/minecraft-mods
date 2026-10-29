package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Makes the game rules a {@link Player} reads about itself per-player.
 *
 * <ul>
 *   <li>{@code isInvulnerableTo}: drowning, fall, fire and freeze damage. {@code this} is the victim.</li>
 *   <li>{@code dropEquipment}: whether the inventory drops on death ({@code keepInventory}).</li>
 *   <li>{@code getBaseExperienceReward}: whether the player's XP drops on death ({@code keepInventory}).</li>
 * </ul>
 *
 * Each target method reads the rule only through {@code GameRules#get}, so one wrapper covers them all and
 * a rule with no override just returns what vanilla read.
 */
@Mixin(Player.class)
public abstract class RFT$PlayerMixin {
    @WrapOperation(
        method = {"isInvulnerableTo", "dropEquipment", "getBaseExperienceReward"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerRule(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked((Player) (Object) this, rule, original.call(rules, rule));
    }
}
