package com.chimericdream.hopperxtreme.network;

import com.chimericdream.hopperxtreme.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** Client to server: "switch the mode of the Hopper Wrench I'm holding". Carries no data; the server knows who sent it. */
public record CycleWrenchModePayload() implements CustomPacketPayload {
    public static final CycleWrenchModePayload INSTANCE = new CycleWrenchModePayload();
    public static final Type<CycleWrenchModePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "cycle_wrench_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleWrenchModePayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<CycleWrenchModePayload> type() {
        return ID;
    }
}
