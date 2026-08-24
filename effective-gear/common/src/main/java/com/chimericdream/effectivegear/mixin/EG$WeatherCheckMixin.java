package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// Channeling's lightning summon is gated by minecraft:weather_check{thundering:true} - vanilla's only use of that condition.
@Mixin(WeatherCheck.class)
public class EG$WeatherCheckMixin {
    @Shadow
    @Final
    private Optional<Boolean> isThundering;

    @Inject(
        method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void eg$allowChannelingWithoutStorm(LootContext context, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isThundering.orElse(false) || context.getLevel().isThundering()) {
            return;
        }

        Player thrower = eg$resolveTridentThrower(context);
        if (thrower != null && TrimSetUtils.isWearingFullTrim(thrower, TrimMaterials.COPPER)) {
            cir.setReturnValue(true);
        }
    }

    private static Player eg$resolveTridentThrower(LootContext context) {
        Entity attacker = context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if (attacker instanceof Player player) {
            return player;
        }

        Entity directAttacker = context.getOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY);
        if (directAttacker instanceof Projectile projectile && projectile.getOwner() instanceof Player owner) {
            return owner;
        }

        Entity thisEntity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (thisEntity instanceof Player player) {
            return player;
        }
        if (thisEntity instanceof Projectile projectile && projectile.getOwner() instanceof Player owner) {
            return owner;
        }

        return null;
    }
}
