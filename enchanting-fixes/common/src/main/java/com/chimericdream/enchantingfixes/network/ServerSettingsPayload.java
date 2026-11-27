package com.chimericdream.enchantingfixes.network;

import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Server to client: the server settings that change what the client shows, and whether the Scaling
 * Enchantments pack is on. The protection limit is not here, because only the server calculates damage.
 */
public record ServerSettingsPayload(
    boolean scalingPack,
    Map<String, Integer> maxLevels,
    boolean useDefaultMaxLevel,
    int defaultMaxLevel,
    boolean defaultAppliesToSingleLevel,
    boolean removeAnvilLimit,
    boolean linearAnvilUpgrades,
    boolean removeIncompatibilities
) implements CustomPacketPayload {
    public static final Type<ServerSettingsPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "server_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerSettingsPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, ServerSettingsPayload::scalingPack,
        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT), ServerSettingsPayload::maxLevels,
        ByteBufCodecs.BOOL, ServerSettingsPayload::useDefaultMaxLevel,
        ByteBufCodecs.VAR_INT, ServerSettingsPayload::defaultMaxLevel,
        ByteBufCodecs.BOOL, ServerSettingsPayload::defaultAppliesToSingleLevel,
        ByteBufCodecs.BOOL, ServerSettingsPayload::removeAnvilLimit,
        ByteBufCodecs.BOOL, ServerSettingsPayload::linearAnvilUpgrades,
        ByteBufCodecs.BOOL, ServerSettingsPayload::removeIncompatibilities,
        ServerSettingsPayload::new
    );

    public static ServerSettingsPayload of(EnchantingFixesConfig config, boolean scalingPack) {
        return new ServerSettingsPayload(
            scalingPack,
            config.maxLevels,
            config.useDefaultMaxLevel,
            config.defaultMaxLevel,
            config.defaultAppliesToSingleLevel,
            config.removeAnvilLimit,
            config.linearAnvilUpgrades,
            config.removeIncompatibilities
        );
    }

    /** A config with the settings from the server. The other settings keep their default values. */
    public EnchantingFixesConfig toConfig() {
        EnchantingFixesConfig config = new EnchantingFixesConfig();
        config.maxLevels = Map.copyOf(maxLevels);
        config.useDefaultMaxLevel = useDefaultMaxLevel;
        config.defaultMaxLevel = defaultMaxLevel;
        config.defaultAppliesToSingleLevel = defaultAppliesToSingleLevel;
        config.removeAnvilLimit = removeAnvilLimit;
        config.linearAnvilUpgrades = linearAnvilUpgrades;
        config.removeIncompatibilities = removeIncompatibilities;
        return config;
    }

    @Override
    public @NotNull Type<ServerSettingsPayload> type() {
        return ID;
    }
}
