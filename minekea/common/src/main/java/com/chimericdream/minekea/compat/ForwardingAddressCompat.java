package com.chimericdream.minekea.compat;

import com.chimericdream.forwardingaddress.api.Migrations;
import dev.architectury.platform.Platform;

import static com.chimericdream.minekea.MinekeaMod.LOGGER;

/**
 * Tells Forwarding Address that Cobblicious blocks replace the Minekea blocks that Minekea skips.
 * <p>
 * This class references the Forwarding Address API. Load it only when {@link #isAvailable()} is true.
 * <p>
 * The old Minekea blocks are not registered when Cobblicious is loaded, so each migration is a plain
 * rename. Both blocks have the same block state properties, so Forwarding Address copies them by name.
 */
public final class ForwardingAddressCompat {
    public static final String FORWARDING_ADDRESS_ID = "forwardingaddress";

    private ForwardingAddressCompat() {
    }

    public static boolean isAvailable() {
        return CobbliciousCompat.isActive() && Platform.isModLoaded(FORWARDING_ADDRESS_ID);
    }

    public static void registerMigrations() {
        CobbliciousCompat.replacements().forEach((minekeaId, cobbliciousId) -> {
            if (minekeaId.equals(CobbliciousCompat.WARPED_WART_ITEM_ID)) {
                // The Warped Wart block and item have different Minekea ids. There is no block with
                // the item id, so this one is an item migration only.
                Migrations.item(minekeaId).to(cobbliciousId).register();
            } else {
                Migrations.block(minekeaId).to(cobbliciousId).register();
            }
        });

        LOGGER.info("Registered {} Cobblicious migrations with Forwarding Address", CobbliciousCompat.replacements().size());
    }
}
