package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Client to server: "use this recipe for this conflict at the station I have open". The server only accepts it if it
 * matches what it last offered that player for that menu, so a forged or stale request is ignored.
 */
public record SelectRecipePayload(int containerId, String groupKey, String recipeId) implements CustomPacketPayload {
    public static final Type<SelectRecipePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "select_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectRecipePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SelectRecipePayload::containerId,
        ByteBufCodecs.STRING_UTF8, SelectRecipePayload::groupKey,
        ByteBufCodecs.STRING_UTF8, SelectRecipePayload::recipeId,
        SelectRecipePayload::new
    );

    @Override
    public @NotNull Type<SelectRecipePayload> type() {
        return ID;
    }
}
