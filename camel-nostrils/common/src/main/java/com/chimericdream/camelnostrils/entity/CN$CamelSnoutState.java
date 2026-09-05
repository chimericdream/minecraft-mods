package com.chimericdream.camelnostrils.entity;

import net.minecraft.world.entity.animal.camel.Camel;

/**
 * Backed by each platform's native data-attachment API (NeoForge's {@code AttachmentType}, Fabric's
 * {@code AttachmentType}/{@code AttachmentTarget}) rather than {@code SynchedEntityData}: NeoForge hard-rejects
 * any {@code SynchedEntityData.defineId()} call whose declaring class isn't the entity class itself
 * ("attempt to add synced data to a foreign entity"), which a {@code @Unique} mixin field can never satisfy
 * even though Mixin merges its initializer into {@code Camel}'s own {@code <clinit>} - see the "Adding synced
 * entity data via mixin" note in the repo root {@code CLAUDE.md} for the full explanation. Attachments
 * sidestep this: they aren't drawn from a fixed-size id space and go through each platform's normal
 * attachment lifecycle instead of {@code SynchedEntityData}'s per-entity-class id pool.
 * <p>
 * Wired via a platform-registered {@link Provider} rather than {@code @ExpectPlatform}, matching
 * {@code CampfireGraceHolder} in sneaky-tweaks: that annotation requires its generated {@code Impl} class to
 * live in this same package on the platform source set, but NeoForge's dev run resolves common and the
 * neoforge source set as separate JPMS modules, so two modules end up exporting this package and FML fails
 * to start.
 */
public final class CN$CamelSnoutState {
    private static Provider provider;

    private CN$CamelSnoutState() {
    }

    public static void setProvider(Provider platformProvider) {
        provider = platformProvider;
    }

    public static boolean hasSnout(Camel camel) {
        return provider.hasSnout(camel);
    }

    public static void setHasSnout(Camel camel, boolean hasSnout) {
        provider.setHasSnout(camel, hasSnout);
    }

    public interface Provider {
        boolean hasSnout(Camel camel);

        void setHasSnout(Camel camel, boolean hasSnout);
    }
}
