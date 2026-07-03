package com.chimericdream.bannertweaks.fabric.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import com.chimericdream.bannertweaks.network.ServerNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class)
public class LoginMixin {
    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V")
    private void syncBannerLayerLimit(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo info) {
        ServerPlayNetworking.send(player, new ServerNetworking.BannerLayerLimitPayload(BannerTweaksConfig.HANDLER.instance().maxBannerLayers));
    }
}
