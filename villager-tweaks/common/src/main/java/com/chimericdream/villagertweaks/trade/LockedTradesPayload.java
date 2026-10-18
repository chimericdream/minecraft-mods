package com.chimericdream.villagertweaks.trade;

import com.chimericdream.villagertweaks.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Server to client, sent right after the vanilla offers packet when a trading screen opens: the trades the
 * villager hasn't unlocked yet, for the client to show greyed out after the real ones.
 */
public record LockedTradesPayload(int containerId, List<LockedTrade> trades) implements CustomPacketPayload {
    public static final Type<LockedTradesPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "locked_trades"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LockedTradesPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, LockedTradesPayload::containerId,
        LockedTrade.CODEC.apply(ByteBufCodecs.list()), LockedTradesPayload::trades,
        LockedTradesPayload::new
    );

    @Override
    public @NotNull Type<LockedTradesPayload> type() {
        return ID;
    }

    /** One locked offer and the villager level that unlocks it. */
    public record LockedTrade(int level, MerchantOffer offer) {
        public static final StreamCodec<RegistryFriendlyByteBuf, LockedTrade> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LockedTrade::level,
            MerchantOffer.STREAM_CODEC, LockedTrade::offer,
            LockedTrade::new
        );
    }
}
