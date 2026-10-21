package com.chimericdream.polycraft.network;

import com.chimericdream.polycraft.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Server to client: the recipes a player can choose between at the station they have open. An empty list means "no
 * choice right now". The client can't work any of this out itself because it doesn't hold the recipes.
 *
 * @param containerId the open menu this belongs to; the client ignores it for any other menu
 * @param selected    index of the recipe the station is currently using
 * @param pinnable    whether this station can pin a recipe to the block (furnaces and the like)
 * @param pinned      whether this group is pinned on the block
 */
public record ConflictOptionsPayload(int containerId, String groupKey, int selected, List<Entry> options, boolean pinnable, boolean pinned) implements CustomPacketPayload {
    public static final Type<ConflictOptionsPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "conflict_options"));

    /** {@code enabled} is false when the station can't take this recipe's result right now. */
    public record Entry(String recipeId, ItemStack result, boolean enabled) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Entry::recipeId,
            ItemStack.STREAM_CODEC, Entry::result,
            ByteBufCodecs.BOOL, Entry::enabled,
            Entry::new
        );

        /** The namespace of the recipe's id, which is the mod it came from. */
        public String namespace() {
            int colon = recipeId.indexOf(':');
            return colon < 0 ? "minecraft" : recipeId.substring(0, colon);
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ConflictOptionsPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ConflictOptionsPayload::containerId,
        ByteBufCodecs.STRING_UTF8, ConflictOptionsPayload::groupKey,
        ByteBufCodecs.VAR_INT, ConflictOptionsPayload::selected,
        Entry.CODEC.apply(ByteBufCodecs.list()), ConflictOptionsPayload::options,
        ByteBufCodecs.BOOL, ConflictOptionsPayload::pinnable,
        ByteBufCodecs.BOOL, ConflictOptionsPayload::pinned,
        ConflictOptionsPayload::new
    );

    public static ConflictOptionsPayload none(int containerId) {
        return new ConflictOptionsPayload(containerId, "", -1, List.of(), false, false);
    }

    public boolean isEmpty() {
        return options.isEmpty();
    }

    @Override
    public @NotNull Type<ConflictOptionsPayload> type() {
        return ID;
    }
}
