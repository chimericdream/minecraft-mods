package com.chimericdream.allhallowssteve.network;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** Client to server: "turn my worn pumpkin to its next face". Carries no data; the server knows who sent it. */
public record CyclePumpkinFacePayload() implements CustomPacketPayload {
    public static final CyclePumpkinFacePayload INSTANCE = new CyclePumpkinFacePayload();
    public static final Type<CyclePumpkinFacePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "cycle_pumpkin_face"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CyclePumpkinFacePayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<CyclePumpkinFacePayload> type() {
        return ID;
    }
}
