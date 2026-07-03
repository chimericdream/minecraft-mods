package com.chimericdream.bannertweaks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ServerNetworking {
    public static void init() {
    }

    public record BannerLayerLimitPayload(int limit) implements CustomPacketPayload {
        public static final Type<BannerLayerLimitPayload> ID = new Type<>(ModPackets.BANNER_LAYER_LIMIT);
        public static final StreamCodec<FriendlyByteBuf, BannerLayerLimitPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, BannerLayerLimitPayload::limit, BannerLayerLimitPayload::new);

        @Override
        public @NotNull Type<BannerLayerLimitPayload> type() {
            return ID;
        }

        public int getLimit() {
            return limit;
        }
    }
}
