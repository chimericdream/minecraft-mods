package com.chimericdream.bannertweaks.network;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ServerNetworking {
    @Nullable
    private static Integer clientConfiguredMaxBannerLayers;

    public static void init() {
    }

    /**
     * Applies a limit pushed by the server we just joined, first remembering the client's own
     * configured value (on the first call since the last {@link #restoreClientLimit()}) so it can
     * be restored on disconnect instead of being permanently clobbered by whatever server was
     * joined last.
     */
    public static void applyServerLimit(int limit) {
        if (clientConfiguredMaxBannerLayers == null) {
            clientConfiguredMaxBannerLayers = BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
        }

        BannerTweaksConfig.HANDLER.instance().maxBannerLayers = limit;
    }

    /** Restores the client's own configured value after disconnecting from a server. */
    public static void restoreClientLimit() {
        if (clientConfiguredMaxBannerLayers != null) {
            BannerTweaksConfig.HANDLER.instance().maxBannerLayers = clientConfiguredMaxBannerLayers;
            clientConfiguredMaxBannerLayers = null;
        }
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
