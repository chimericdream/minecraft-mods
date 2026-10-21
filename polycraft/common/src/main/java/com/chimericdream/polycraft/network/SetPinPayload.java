package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Client to server: pin the recipe the station is using for this conflict to the block, or remove the pin. The server
 * pins whatever it last offered as selected, so the client can't pin a recipe that wasn't offered.
 */
public record SetPinPayload(int containerId, String groupKey, boolean pinned) implements CustomPacketPayload {
    public static final Type<SetPinPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "set_pin"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPinPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SetPinPayload::containerId,
        ByteBufCodecs.STRING_UTF8, SetPinPayload::groupKey,
        ByteBufCodecs.BOOL, SetPinPayload::pinned,
        SetPinPayload::new
    );

    @Override
    public @NotNull Type<SetPinPayload> type() {
        return ID;
    }
}
