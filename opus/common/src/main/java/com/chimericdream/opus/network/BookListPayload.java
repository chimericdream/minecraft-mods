package com.chimericdream.opus.network;

import com.chimericdream.opus.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Client to server: "these are the books my resource packs provide", used to suggest ids in {@code /opus give}. */
public record BookListPayload(List<Identifier> books) implements CustomPacketPayload {
    public static final Type<BookListPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BookListPayload> CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), BookListPayload::books,
        BookListPayload::new
    );

    @Override
    public @NotNull Type<BookListPayload> type() {
        return ID;
    }
}
