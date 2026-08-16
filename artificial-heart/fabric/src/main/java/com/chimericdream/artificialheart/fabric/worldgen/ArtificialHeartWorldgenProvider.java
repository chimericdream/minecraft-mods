package com.chimericdream.artificialheart.fabric.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ArtificialHeartWorldgenProvider extends FabricDynamicRegistryProvider {
    public ArtificialHeartWorldgenProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
    }

    @Override
    public @NonNull String getName() {
        return "Artificial Heart World Generation";
    }
}
