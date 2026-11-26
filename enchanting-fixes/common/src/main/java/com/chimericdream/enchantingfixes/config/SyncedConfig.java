package com.chimericdream.enchantingfixes.config;

import org.jspecify.annotations.Nullable;

/**
 * The config that the game logic uses. A client that is connected to another server uses the settings that
 * the server sent (see {@code SettingsSync}). Everywhere else, this is the local config: on a dedicated
 * server, and in single player, where the client and the integrated server already share the config.
 */
public final class SyncedConfig {
    private static volatile @Nullable EnchantingFixesConfig fromServer = null;

    private SyncedConfig() {
    }

    public static EnchantingFixesConfig effective() {
        EnchantingFixesConfig synced = fromServer;
        return synced != null ? synced : EnchantingFixesConfig.CONFIG.instance();
    }

    /** For the client only: stores the settings that the server sent, or null when the player leaves. */
    public static void setFromServer(@Nullable EnchantingFixesConfig config) {
        fromServer = config;
    }
}
