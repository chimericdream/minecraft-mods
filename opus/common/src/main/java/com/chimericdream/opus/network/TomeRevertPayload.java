package com.chimericdream.opus.network;

import com.chimericdream.opus.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** Client to server: "turn the guide in my hand back into its Book of Books". Carries no data; the server knows who sent it. */
public record TomeRevertPayload() implements CustomPacketPayload {
    public static final TomeRevertPayload INSTANCE = new TomeRevertPayload();
    public static final Type<TomeRevertPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_revert"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TomeRevertPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<TomeRevertPayload> type() {
        return ID;
    }
}
