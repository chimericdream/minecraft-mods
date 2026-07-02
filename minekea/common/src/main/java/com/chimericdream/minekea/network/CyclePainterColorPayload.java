package com.chimericdream.minekea.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CyclePainterColorPayload(boolean cyclePressed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CyclePainterColorPayload> ID = new CustomPacketPayload.Type<>(ServerNetworking.CYCLE_PAINTER_COLOR);
    public static final StreamCodec<RegistryFriendlyByteBuf, CyclePainterColorPayload> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, CyclePainterColorPayload::cyclePressed, CyclePainterColorPayload::new);

    @Override
    public Type<CyclePainterColorPayload> type() {
        return ID;
    }
}
