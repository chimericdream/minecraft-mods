package com.chimericdream.stackitup.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ByteArrayPayload(byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ByteArrayPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("stackitup", "config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ByteArrayPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY, ByteArrayPayload::data,
            ByteArrayPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
