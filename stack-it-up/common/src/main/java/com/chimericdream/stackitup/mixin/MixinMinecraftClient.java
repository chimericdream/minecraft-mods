package com.chimericdream.stackitup.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.stackitup.client.ConfigSync;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {
    // The old no-arg disconnect() is gone as of 1.21.11; every disconnect path (including
    // transfers) now funnels through this 3-arg overload, so RETURN here fires unconditionally
    // just like the old injection did.
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("RETURN"))
    private void resetMaxCount(Screen disconnectionScreen, boolean transferring, boolean stopSounds, CallbackInfo ci) {
        ConfigSync.resetConfig();
    }
}
