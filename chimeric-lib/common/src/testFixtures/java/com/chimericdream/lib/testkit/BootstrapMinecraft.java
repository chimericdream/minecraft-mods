package com.chimericdream.lib.testkit;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class for unit tests that touch Minecraft registries, items, blocks, or tags.
 *
 * <p>This lives in chimeric-lib's {@code testFixtures} source set so every consumer mod can reuse it
 * instead of copying the bootstrap logic. In this repo's own build, chimeric-lib's fabric tests pull
 * it in via {@code testImplementation(testFixtures(project(":chimeric-lib:common")))}; a downstream
 * mod adds it to its fabric {@code test} source set with:
 *
 * <pre>{@code
 * testImplementation(testFixtures("com.chimericdream.lib:chimericlib-common-<mc>:<version>"))
 * }</pre>
 *
 * <p>Tests run outside a live game, so the static registries ({@code Items}, {@code Blocks},
 * {@code BuiltInRegistries}, ...) are unpopulated until Minecraft's bootstrap runs. The
 * {@code fabric-loader-junit} dependency (wired into the fabric subproject's {@code test} source
 * set) puts the Fabric loader and the named Minecraft jar on the test classpath; this base then
 * triggers the vanilla bootstrap so registry lookups resolve.
 *
 * <p>As of MC 26.2 an item's data components are data-driven: {@link Bootstrap#bootStrap()}
 * populates the registries but leaves each {@code Holder.Reference}'s components <em>unbound</em>,
 * so constructing an {@link net.minecraft.world.item.ItemStack} fails with "Components not bound
 * yet". We bake the components from the full vanilla lookup once, mirroring what a loading
 * server/data pack does, so headless {@code ItemStack} construction works.
 *
 * <p>The bootstrap calls are idempotent and the component bake is guarded by {@link #baked}, so it
 * is safe for every subclass to run this {@code @BeforeAll}. Pure-logic tests (string/bit math,
 * {@code Direction} tables, {@code Component} building) do <em>not</em> need this and should not
 * extend it.
 */
public abstract class BootstrapMinecraft {
    private static boolean baked = false;

    @BeforeAll
    static synchronized void bootstrapMinecraft() {
        if (baked) {
            return;
        }

        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        HolderLookup.Provider provider = VanillaRegistries.createLookup();
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(provider)
            .forEach(pending -> pending.apply());

        baked = true;
    }
}
