package com.chimericdream.rulesforthee.mixin;

import com.chimericdream.rulesforthee.rule.ClientSync;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After vanilla broadcasts a changed world rule to every player, re-send the rules the client caches to the
 * players who override them, so a {@code /gamerule} change doesn't wipe their own values.
 */
@Mixin(MinecraftServer.class)
public abstract class RFT$MinecraftServerMixin {
    @Inject(method = "onGameRuleChanged", at = @At("TAIL"))
    private void rft$resendOverrides(GameRule<?> rule, Object value, CallbackInfo ci) {
        ClientSync.afterWorldRuleChanged((MinecraftServer) (Object) this, rule);
    }
}
