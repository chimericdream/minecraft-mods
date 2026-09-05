package com.chimericdream.effectivegear.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record UseAbilityPayload() implements CustomPacketPayload {
    public static final UseAbilityPayload INSTANCE = new UseAbilityPayload();
    public static final CustomPacketPayload.Type<UseAbilityPayload> ID = new CustomPacketPayload.Type<>(ServerNetworking.USE_ABILITY);
    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<UseAbilityPayload> type() {
        return ID;
    }
}
