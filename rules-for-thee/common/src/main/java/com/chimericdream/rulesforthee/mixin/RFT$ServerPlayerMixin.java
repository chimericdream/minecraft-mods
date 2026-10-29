package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Per-player rules read inside {@link ServerPlayer}:
 *
 * <ul>
 *   <li>{@code die}: whether the player's death is announced ({@code showDeathMessages}).</li>
 *   <li>{@code restoreFrom}: whether the respawned player is given the old inventory, XP and score back
 *       ({@code keepInventory}). In this method {@code this} is the new player; the UUID is the same.</li>
 * </ul>
 */
@Mixin(ServerPlayer.class)
public abstract class RFT$ServerPlayerMixin {
    @WrapOperation(
        method = {"die", "restoreFrom"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        )
    )
    private Object rft$perPlayerRule(GameRules rules, GameRule<?> rule, Operation<Object> original) {
        return PlayerRules.resolveUnchecked((ServerPlayer) (Object) this, rule, original.call(rules, rule));
    }
}
