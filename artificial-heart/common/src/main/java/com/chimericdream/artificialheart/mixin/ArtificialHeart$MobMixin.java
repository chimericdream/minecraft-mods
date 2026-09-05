package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.PassiveCreakingAccessor;
import com.chimericdream.artificialheart.loot.ArtificialHeartLootTables;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * {@code Mob#getLootTable} is final and overrides {@code Entity#getLootTable}, so for any Mob subclass
 * (including Creaking) it's Mob's version that actually runs - injecting into Entity's copy of the
 * method would never fire. This has to target Mob directly.
 */
@Mixin(Mob.class)
public class ArtificialHeart$MobMixin {
    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void ah$overridePassiveCreakingLootTable(final CallbackInfoReturnable<Optional<ResourceKey<LootTable>>> cir) {
        if (this instanceof PassiveCreakingAccessor accessor && accessor.ah$isPassive()) {
            cir.setReturnValue(Optional.of(ArtificialHeartLootTables.PASSIVE_CREAKING));
        }
    }
}
