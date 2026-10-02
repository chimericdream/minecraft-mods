package com.chimericdream.allhallowssteve.network;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** Server to clients: which face {@code player}'s worn pumpkin is turned to. */
public record PumpkinFacePayload(UUID player, Direction face) implements CustomPacketPayload {
    public static final Type<PumpkinFacePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pumpkin_face"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PumpkinFacePayload> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, PumpkinFacePayload::player,
        ByteBufCodecs.idMapper(Direction::from2DDataValue, Direction::get2DDataValue), PumpkinFacePayload::face,
        PumpkinFacePayload::new
    );

    @Override
    public @NotNull Type<PumpkinFacePayload> type() {
        return ID;
    }
}
