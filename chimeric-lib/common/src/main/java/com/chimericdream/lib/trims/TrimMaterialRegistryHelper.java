package com.chimericdream.lib.trims;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

/**
 * Registers a mod's {@link TrimMaterialConfig} list into the {@code trim_material} dynamic registry,
 * the same shape vanilla's own {@code TrimMaterials.bootstrap} uses. Loader-agnostic: both Fabric
 * (via {@code FabricDataGenerator.buildRegistry}) and NeoForge (via
 * {@code RegistrySetBuilder.add(Registries.TRIM_MATERIAL, ...)}) only need a
 * {@code Consumer<BootstrapContext<TrimMaterial>>}, so this same method serves both.
 */
public class TrimMaterialRegistryHelper {
    private TrimMaterialRegistryHelper() {
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context, List<TrimMaterialConfig> materials) {
        for (TrimMaterialConfig config : materials) {
            ResourceKey<TrimMaterial> key = ResourceKey.create(
                Registries.TRIM_MATERIAL,
                Identifier.fromNamespaceAndPath(config.namespace(), config.id())
            );

            Component description = Component.translatable(config.translationKey()).withStyle(Style.EMPTY.withColor(config.color()));

            context.register(key, new TrimMaterial(MaterialAssetGroup.create(config.assetName()), description));
        }
    }
}
