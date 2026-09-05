package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.PassiveCreakingAccessor;
import com.chimericdream.artificialheart.block.ModBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ArtificialHeart$EntityMixin {
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void ah$checkPlayerWearingPalePumpkin(final @Nullable Entity other, final CallbackInfoReturnable<Boolean> cir) {
        if (
            this instanceof PassiveCreakingAccessor
                && other instanceof Player player
                && player.getItemBySlot(EquipmentSlot.HEAD).is(ModBlocks.PALE_CARVED_PUMPKIN_BLOCK.get().asItem())
        ) {
            cir.setReturnValue(true);
        }
    }
}
