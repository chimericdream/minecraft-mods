package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.network.StationViewers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Notes who clicked in a menu last, so a furnace with several viewers knows who is using it. */
@Mixin(AbstractContainerMenu.class)
public class PCAbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"))
    private void polycraft$noteClick(int slotIndex, int buttonNum, ContainerInput input, Player player, CallbackInfo ci) {
        if (!player.level().isClientSide()) {
            StationViewers.touch((AbstractContainerMenu) (Object) this);
        }
    }
}
