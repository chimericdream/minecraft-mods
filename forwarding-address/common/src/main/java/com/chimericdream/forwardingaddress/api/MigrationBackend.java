package com.chimericdream.forwardingaddress.api;

/**
 * The implementation side of the API. Forwarding Address installs one at startup through
 * {@link Migrations#installBackend(MigrationBackend)}; the API package never refers to anything else
 * in the mod, so it can be split into its own artifact later.
 */
public interface MigrationBackend {
    void register(BlockMigration migration);

    void register(ItemMigration migration);
}
