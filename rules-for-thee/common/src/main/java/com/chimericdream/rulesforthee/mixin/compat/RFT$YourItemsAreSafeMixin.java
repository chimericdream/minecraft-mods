package com.chimericdream.rulesforthee.mixin.compat;

import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Compatibility with Serilum's <i>Your Items Are Safe</i>. Its death handler reads {@code keepInventory}
 * from the level, so it never sees a player's override: it would build a grave for a player who keeps their
 * inventory, and skip one for a player who doesn't. It reads the rule once, at the top of
 * {@code onPlayerDeath}, and doesn't read it again in its delayed task, so resolving that one read for the
 * dying player is enough.
 *
 * <p>Applied only when the mod is present (see {@code CompatMixinPlugin}). The injector is {@code require = 0}
 * so a future update of that mod that moves the read logs a warning instead of stopping the game.
 */
@Pseudo
@Mixin(targets = "com.serilum.youritemsaresafe.events.DeathEvent")
public abstract class RFT$YourItemsAreSafeMixin {
    @WrapOperation(
        method = "onPlayerDeath",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
        ),
        require = 0
    )
    private static Object rft$perPlayerKeepInventory(GameRules rules, GameRule<?> rule, Operation<Object> original, @Local(argsOnly = true) ServerPlayer player) {
        return PlayerRules.resolveUnchecked(player, rule, original.call(rules, rule));
    }
}
