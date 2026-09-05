package com.chimericdream.sneakytweaks.campfire;

import net.minecraft.world.entity.player.Player;

/**
 * Backed by each platform's native data-attachment API (NeoForge's {@code AttachmentType}, Fabric's
 * {@code AttachmentType}/{@code AttachmentTarget}) rather than {@code SynchedEntityData}. A previous
 * {@code SynchedEntityData}-based version of this ran into two dead ends: defining the id lazily (on
 * first use, from a separate interface) raced against the already-fixed-size backing array allocated
 * for Player's tracked data, throwing ArrayIndexOutOfBoundsException depending on when the JVM's first
 * Player got constructed; and NeoForge separately hard-rejects any {@code SynchedEntityData.defineId()}
 * call whose declaring class isn't the entity class itself ("attempt to add synced data to a foreign
 * entity"), which a plain interface can never satisfy. Attachments sidestep both: they aren't drawn
 * from a fixed-size id space, and registration goes through each platform's normal lifecycle instead of
 * racing class initializers.
 * <p>
 * Wired via a platform-registered {@link Provider} rather than {@code @ExpectPlatform}: that annotation
 * requires its generated {@code Impl} class to live in this same package on the platform source set,
 * but NeoForge's dev run resolves common and the neoforge source set as separate JPMS modules, so two
 * modules end up exporting this package and FML fails to start ("Modules ... export package
 * ...campfire to module ...", ResolutionException). A provider interface keeps each platform's
 * implementation in its own {@code fabric}/{@code neoforge} package instead.
 */
public final class CampfireGraceHolder {
    private static Provider provider;

    private CampfireGraceHolder() {
    }

    public static void setProvider(Provider platformProvider) {
        provider = platformProvider;
    }

    public static int getCampfireGraceTicks(Player player) {
        return provider.getCampfireGraceTicks(player);
    }

    public static void setCampfireGraceTicks(Player player, int ticks) {
        provider.setCampfireGraceTicks(player, ticks);
    }

    public interface Provider {
        int getCampfireGraceTicks(Player player);

        void setCampfireGraceTicks(Player player, int ticks);
    }
}
