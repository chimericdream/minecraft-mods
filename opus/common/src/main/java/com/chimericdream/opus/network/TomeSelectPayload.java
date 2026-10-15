package com.chimericdream.opus.network;

import com.chimericdream.opus.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** Client to server: "turn the Book of Books in this hand into the guide at this index". */
public record TomeSelectPayload(boolean offHand, int index) implements CustomPacketPayload {
    public static final Type<TomeSelectPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_select"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomeSelectPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, TomeSelectPayload::offHand,
        ByteBufCodecs.VAR_INT, TomeSelectPayload::index,
        TomeSelectPayload::new
    );

    @Override
    public @NotNull Type<TomeSelectPayload> type() {
        return ID;
    }
}
